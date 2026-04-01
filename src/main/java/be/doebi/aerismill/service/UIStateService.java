package be.doebi.aerismill.service;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

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
        if (root == null) return;

        walkAndRestoreTabs(root);

        Platform.runLater(() -> walkAndRestoreSplitPanes(root));
    }

    private void walkAndRestoreTabs(Node node) {
        if (node.getId() != null && !node.getId().isBlank()) {
            if (node instanceof TabPane tabPane) {
                restoreTabPane(node.getId(), tabPane);
            }
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                walkAndRestoreTabs(child);
            }
        }
    }

    private void walkAndRestoreSplitPanes(Node node) {
        if (node.getId() != null && !node.getId().isBlank()) {
            if (node instanceof SplitPane splitPane) {
                restoreSplitPane(node.getId(), splitPane);
            }
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                walkAndRestoreSplitPanes(child);
            }
        }
    }

    private void walkAndSave(Node node) {
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
            } else if (node instanceof TabPane tabPane) {
                restoreTabPane(node.getId(), tabPane);
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


    public void saveWindowState(Stage stage) {
        if (stage == null) return;

        prefs.putDouble("window.x", stage.getX());
        prefs.putDouble("window.y", stage.getY());
        prefs.putDouble("window.width", stage.getWidth());
        prefs.putDouble("window.height", stage.getHeight());
        prefs.putBoolean("window.maximized", stage.isMaximized());
    }

    public void restoreWindowState(Stage stage) {
        if (stage == null) return;

        double width = prefs.getDouble("window.width", 1200);
        double height = prefs.getDouble("window.height", 800);
        double x = prefs.getDouble("window.x", Double.NaN);
        double y = prefs.getDouble("window.y", Double.NaN);
        boolean maximized = prefs.getBoolean("window.maximized", false);

        stage.setWidth(width);
        stage.setHeight(height);

        if (!Double.isNaN(x)) {
            stage.setX(x);
        }
        if (!Double.isNaN(y)) {
            stage.setY(y);
        }

        stage.setMaximized(maximized);
    }
}
