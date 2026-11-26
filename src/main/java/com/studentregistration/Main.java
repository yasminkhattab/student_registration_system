package com.studentregistration;

import com.studentregistration.config.AppContext;
import com.studentregistration.util.AlertHelper;
import com.studentregistration.util.SceneNavigator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Application entry point.
 *
 * <p>Bootstraps persistence and services, then opens the JavaFX login screen.
 * Run it with {@code mvn javafx:run}.</p>
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            AppContext context = AppContext.bootstrap();
            SceneNavigator navigator = new SceneNavigator(stage, context);
            context.setNavigator(navigator);
            navigator.showLogin();
        } catch (IOException | RuntimeException ex) {
            AlertHelper.showFatal(null, "Startup failed",
                    "The application could not start. Check the data directory and try again.",
                    stackTrace(ex));
            Platform.exit();
        }
    }

    /**
     * Standard JavaFX launcher.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }

    private static String stackTrace(Throwable throwable) {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
