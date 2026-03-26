package be.doebi.aerismill.parser.step;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.topology.AdvancedFace;
import be.doebi.aerismill.model.step.topology.ClosedShell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class ClosedShellParser implements EntityParser<ClosedShell> {
    @Override
    public ClosedShell parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        List<AdvancedFace> cfsFaces = resolveAdvancedFaceList(params.get(1), parsedEntities);

        return new ClosedShell(
                entity.getId(),
                entity.getRawParameters(),
                name,
                cfsFaces
        );
    }



    private List<AdvancedFace> resolveAdvancedFaceList(String token, Map<String, Object> parsedEntities) {
        List<AdvancedFace> result = new ArrayList<>();

        if (token == null || token.equals("$")) {
            return result;
        }

        String trimmed = token.trim();

        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }

        if (trimmed.isBlank()) {
            return result;
        }

        String[] refs = trimmed.split(",");

        for (String ref : refs) {
            String cleanRef = ref.trim();
            AdvancedFace face = (AdvancedFace) parsedEntities.get(cleanRef);
            if (face != null) {
                result.add(face);
            }
        }

        return result;
    }
}
