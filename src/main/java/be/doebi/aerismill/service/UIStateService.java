package be.doebi.aerismill.service;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;

import java.util.prefs.Preferences;

public class UIStateService {
    private static final UIStateService INSTANCE = new UIStateService();
    private final Preferences prefs = Preferences.userNodeForPackage(UIStateService.class);
    private UIStateService() {
    }

    public static UIStateService getInstance() {
        return INSTANCE;
    }

    public void saveLayoutState(Parent root) {
        walkAndSave(root);
    }

    public void restoreLayoutState(Parent root) {
        walkAndRestore(root);
    }

    private void walkAndSave(Node node) {
        System.out.println("Walk and save");
        if (node !=null) {
            if (node.getId() != null && !node.getId().isBlank()) {
                if (node instanceof SplitPane splitPane) {
                    saveSplitPane(node.getId(), splitPane);
                } else if (node instanceof TabPane tabPane) {
                    saveTabPane(node.getId(), tabPane);
                }
            }

            if (node instanceof Parent parent) {
                for (Node child : parent.getChildrenUnmodifiable()) {
                    walkAndSave(child);
                }
            }
        } else {
            System.out.println("node is null");
        }
    }

    private void walkAndRestore(Node node) {
        if (node.getId() != null && !node.getId().isBlank()) {
            if (node instanceof SplitPane splitPane) {
                restoreSplitPane(node.getId(), splitPane);
                System.out.println("Restore splitpane" + node.toString());
            } else if (node instanceof TabPane tabPane) {
                restoreTabPane(node.getId(), tabPane);
                System.out.println("Restore tabPane");
            }
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                walkAndRestore(child);
            }
        }
    }

    private void saveSplitPane(String key, SplitPane splitPane) {
        double[] dividers = splitPane.getDividerPositions();
        for (int i = 0; i < dividers.length; i++) {
            prefs.putDouble(key + ".divider." + i, dividers[i]);
        }
    }

    private void restoreSplitPane(String key, SplitPane splitPane) {
        double[] current = splitPane.getDividerPositions();
        double[] restored = new double[current.length];

        for (int i = 0; i < current.length; i++) {
            restored[i] = prefs.getDouble(key + ".divider." + i, current[i]);
        }

        splitPane.setDividerPositions(restored);
    }

    private void saveTabPane(String key, TabPane tabPane) {
        prefs.putInt(key + ".selectedIndex", tabPane.getSelectionModel().getSelectedIndex());
    }

    private void restoreTabPane(String key, TabPane tabPane) {
        int index = prefs.getInt(key + ".selectedIndex", 0);
        if (index >= 0 && index < tabPane.getTabs().size()) {
            tabPane.getSelectionModel().select(index);
        }
    }
}
