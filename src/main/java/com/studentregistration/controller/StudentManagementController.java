package com.studentregistration.controller;

import com.studentregistration.config.AppContext;
import com.studentregistration.exception.RegistrationException;
import com.studentregistration.model.Student;
import com.studentregistration.util.AlertHelper;
import com.studentregistration.util.Validator;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Administrator screen for adding, editing, deleting and searching students
 * ({@code student-management.fxml}).
 */
public class StudentManagementController {

    @FXML private TextField searchField;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> phoneColumn;
    @FXML private TableColumn<Student, String> majorColumn;
    @FXML private TableColumn<Student, Integer> levelColumn;

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField majorField;
    @FXML private TextField levelField;

    @FXML private Label formTitleLabel;
    @FXML private Label messageLabel;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private final AppContext context;
    private Student selectedStudent;

    /**
     * @param context shared application context
     */
    public StudentManagementController(AppContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId()));
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        emailColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));
        phoneColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPhone()));
        majorColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMajor()));
        levelColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getLevel()).asObject());

        studentTable.setPlaceholder(new Label("No students match the current search."));
        studentTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateForm(newValue));

        clearForm();
        refreshTable(null);
    }

    private void refreshTable(String keyword) {
        studentTable.setItems(FXCollections.observableArrayList(
                context.getStudentService().search(keyword)));
    }

    private void populateForm(Student student) {
        selectedStudent = student;
        if (student == null) {
            clearForm();
            return;
        }
        idField.setText(student.getId());
        idField.setDisable(true);
        nameField.setText(student.getName());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhone());
        majorField.setText(student.getMajor());
        levelField.setText(String.valueOf(student.getLevel()));
        formTitleLabel.setText("Editing " + student.getId());
        addButton.setDisable(true);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
        showMessage("", false);
    }

    private void clearForm() {
        selectedStudent = null;
        studentTable.getSelectionModel().clearSelection();
        idField.clear();
        idField.setDisable(false);
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        majorField.clear();
        levelField.clear();
        formTitleLabel.setText("Add a new student");
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        showMessage("", false);
    }

    @FXML
    private void handleAdd() {
        try {
            context.getStudentService().addStudent(
                    idField.getText(),
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    majorField.getText(),
                    Validator.requireIntInRange(levelField.getText(), "Level", 1, 8));
            refreshTable(searchField.getText());
            clearForm();
            showMessage("Student added successfully.", false);
        } catch (RegistrationException ex) {
            showMessage(ex.getMessage(), true);
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedStudent == null) {
            showMessage("Select a student from the table first.", true);
            return;
        }
        try {
            context.getStudentService().updateStudent(
                    selectedStudent.getId(),
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    majorField.getText(),
                    Validator.requireIntInRange(levelField.getText(), "Level", 1, 8));
            String id = selectedStudent.getId();
            refreshTable(searchField.getText());
            clearForm();
            showMessage("Student " + id + " updated.", false);
        } catch (RegistrationException ex) {
            showMessage(ex.getMessage(), true);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedStudent == null) {
            showMessage("Select a student from the table first.", true);
            return;
        }
        boolean confirmed = AlertHelper.confirm(window(), "Delete student",
                "Delete " + selectedStudent.getName() + " (" + selectedStudent.getId() + ")?");
        if (!confirmed) {
            return;
        }
        try {
            String id = selectedStudent.getId();
            context.getRegistrationService().deleteStudent(id);
            refreshTable(searchField.getText());
            clearForm();
            showMessage("Student " + id + " deleted.", false);
        } catch (RegistrationException ex) {
            showMessage(ex.getMessage(), true);
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    @FXML
    private void handleSearch() {
        refreshTable(searchField.getText());
        showMessage("Showing " + studentTable.getItems().size() + " student(s).", false);
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        refreshTable(null);
        showMessage("", false);
    }

    @FXML
    private void handleBack() {
        context.getNavigator().showAdminDashboard();
    }

    private void showMessage(String message, boolean error) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("status-label",
                error ? "status-error" : "status-success");
    }

    private javafx.stage.Window window() {
        return studentTable.getScene() == null ? null : studentTable.getScene().getWindow();
    }
}
