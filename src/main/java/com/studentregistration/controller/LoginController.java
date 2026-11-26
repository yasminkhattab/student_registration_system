package com.studentregistration.controller;

import com.studentregistration.config.AppConfig;
import com.studentregistration.config.AppContext;
import com.studentregistration.exception.RegistrationException;
import com.studentregistration.exception.ValidationException;
import com.studentregistration.model.Student;
import com.studentregistration.util.Validator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the login screen ({@code login.fxml}).
 *
 * <p>Students sign in with their student ID. Administrators use the built-in
 * administrator account. Only screen flow lives here; lookups and validation are
 * delegated to the services.</p>
 */
public class LoginController {

    private static final String ROLE_STUDENT = "Student";
    private static final String ROLE_ADMINISTRATOR = "Administrator";

    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private Label passwordLabel;
    @FXML private Label messageLabel;
    @FXML private Button loginButton;

    private final AppContext context;

    /**
     * @param context shared application context
     */
    public LoginController(AppContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        roleComboBox.getItems().setAll(ROLE_STUDENT, ROLE_ADMINISTRATOR);
        roleComboBox.getSelectionModel().select(ROLE_STUDENT);
        roleComboBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> updatePasswordVisibility());
        updatePasswordVisibility();
        Platform.runLater(idField::requestFocus);
    }

    private void updatePasswordVisibility() {
        boolean administrator = ROLE_ADMINISTRATOR.equals(roleComboBox.getValue());
        passwordField.setDisable(!administrator);
        passwordField.setVisible(administrator);
        passwordField.setManaged(administrator);
        passwordLabel.setVisible(administrator);
        passwordLabel.setManaged(administrator);
        if (!administrator) {
            passwordField.clear();
        }
        idField.setPromptText(administrator ? AppConfig.ADMIN_ID : "STU-1001");
        messageLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        messageLabel.setText("");
        try {
            if (ROLE_ADMINISTRATOR.equals(roleComboBox.getValue())) {
                signInAdministrator();
            } else {
                signInStudent();
            }
        } catch (RegistrationException ex) {
            messageLabel.setText(ex.getMessage());
        }
    }

    private void signInAdministrator() {
        if (!AppConfig.isValidAdminCredentials(idField.getText(), passwordField.getText())) {
            throw new ValidationException("Invalid administrator ID or password.");
        }
        context.setCurrentUser(AppConfig.createAdministrator());
        context.getNavigator().showAdminDashboard();
    }

    private void signInStudent() {
        String studentId = Validator.requireText(idField.getText(), "Student ID");
        Student student = context.getStudentService().findById(studentId);
        context.setCurrentUser(student);
        context.getNavigator().showStudentDashboard();
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }
}
