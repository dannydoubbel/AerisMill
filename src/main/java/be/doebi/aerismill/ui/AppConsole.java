package be.doebi.aerismill.ui;

import javafx.application.Platform;

import java.util.function.Consumer;

public class AppConsole {
    private static Consumer<String> consoleConsumer;

    private AppConsole() {
    }

    public static void setConsoleConsumer(Consumer<String> consumer) {
        consoleConsumer = consumer;
    }

    public static void log(String message) {
        System.out.println(message);

        if (consoleConsumer != null) {
            Platform.runLater(() -> consoleConsumer.accept(message));
        }
    }
}
