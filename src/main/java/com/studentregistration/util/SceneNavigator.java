package com.studentregistration.util;

import com.studentregistration.config.AppConfig;
import com.studentregistration.config.AppContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

/**
 * Loads FXML screens into the single application window.
 *
 * <p>Controllers are created by a factory that injects the shared
 * {@link AppContext}, so no controller needs a static singleton.</p>
 */
public class SceneNavigator {

    private static final String FXML_DIRECTORY = "/fxml/";
    private static final String STYLESHEET = "/css/styles.css";

    private final Stage stage;
    private final AppContext context;

    /**
     * @param stage   primary application window
     * @param context shared application context
     */
    public SceneNavigator(Stage stage, AppContext context) {
        this.stage = Objects.requireNonNull(stage, "stage");
        this.context = Objects.requireNonNull(context, "context");
    }

    public void showLogin() {
        show("login.fxml", "Sign in");
    }

    public void showAdminDashboard() {
        show("admin-dashboard.fxml", "Administrator dashboard");
    }

    public void showStudentManagement() {
        show("student-management.fxml", "Student management");
    }

    public void showCourseManagement() {
        show("course-management.fxml", "Course management");
    }

    public void showStudentDashboard() {
        show("student-dashboard.fxml", "Student dashboard");
    }

    public void showCourseCatalog() {
        show("course-catalog.fxml", "Course catalogue");
    }

    public void showMyCourses() {
        show("my-courses.fxml", "My registered courses");
    }

    /**
     * Replaces the current scene, reusing the primary stage.
     *
     * @param fxmlFile file inside {@code /fxml/}
     * @param title    suffix shown in the window title
     */
    private void show(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(FXML_DIRECTORY + fxmlFile),
                            "Missing FXML file: " + fxmlFile));
            loader.setControllerFactory(this::createController);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1120, 740);
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource(STYLESHEET),
                            "Missing stylesheet: " + STYLESHEET).toExternalForm());

            stage.setTitle(AppConfig.APP_NAME + " | " + title);
            stage.setScene(scene);
            stage.setMinWidth(980);
            stage.setMinHeight(660);
            if (!stage.isShowing()) {
                stage.centerOnScreen();
                stage.show();
            }
        } catch (IOException ex) {
            String details = stackTrace(ex);
            // Keep the diagnostic visible on the console as well: the alert may
            // not be readable (or even displayable) during start-up.
            System.err.println("Failed to load screen " + fxmlFile);
            ex.printStackTrace();
            AlertHelper.showFatal(stage, "Cannot open screen",
                    "The screen " + fxmlFile + " could not be loaded.", details);
        }
    }

    private Object createController(Class<?> controllerClass) {
        try {
            return controllerClass.getConstructor(AppContext.class).newInstance(context);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(
                    "Controller " + controllerClass.getName()
                            + " must declare a public constructor accepting AppContext.", ex);
        }
    }

    private static String stackTrace(Throwable throwable) {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
