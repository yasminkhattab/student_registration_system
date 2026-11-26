package com.studentregistration.controller;

import com.studentregistration.config.AppContext;
import com.studentregistration.exception.RegistrationException;
import com.studentregistration.model.Course;
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
 * Administrator screen for the course catalogue, including the enrolled-student
 * panel ({@code course-management.fxml}).
 */
public class CourseManagementController {

    @FXML private TextField searchField;
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> idColumn;
    @FXML private TableColumn<Course, String> titleColumn;
    @FXML private TableColumn<Course, Integer> creditsColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, String> scheduleColumn;
    @FXML private TableColumn<Course, Integer> enrolledColumn;
    @FXML private TableColumn<Course, Integer> capacityColumn;

    @FXML private TextField courseIdField;
    @FXML private TextField titleField;
    @FXML private TextField creditsField;
    @FXML private TextField capacityField;
    @FXML private TextField instructorField;
    @FXML private TextField scheduleField;

    @FXML private Label formTitleLabel;
    @FXML private Label messageLabel;
    @FXML private Label enrollmentCourseLabel;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button dropEnrollmentButton;

    @FXML private TableView<Student> enrollmentTable;
    @FXML private TableColumn<Student, String> studentIdColumn;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private TableColumn<Student, String> studentMajorColumn;
    @FXML private TableColumn<Student, Integer> studentLevelColumn;

    private final AppContext context;
    private Course selectedCourse;

    /**
     * @param context shared application context
     */
    public CourseManagementController(AppContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCourseId()));
        titleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTitle()));
        creditsColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getCreditHours()).asObject());
        instructorColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getInstructor()));
        scheduleColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getSchedule()));
        enrolledColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getEnrollmentCount()).asObject());
        capacityColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getCapacity()).asObject());

        studentIdColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId()));
        studentNameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        studentMajorColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMajor()));
        studentLevelColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getLevel()).asObject());

        courseTable.setPlaceholder(new Label("No courses match the current search."));
        enrollmentTable.setPlaceholder(new Label("Select a course to see its enrolled students."));
        courseTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateForm(newValue));

        clearForm();
        refreshTable(null);
    }

    private void refreshTable(String keyword) {
        courseTable.setItems(FXCollections.observableArrayList(
                context.getCourseService().search(keyword)));
    }

    private void refreshEnrollments() {
        if (selectedCourse == null) {
            enrollmentCourseLabel.setText("Select a course to see its enrolled students.");
            enrollmentTable.setItems(FXCollections.observableArrayList());
            dropEnrollmentButton.setDisable(true);
            return;
        }
        enrollmentCourseLabel.setText("Enrolled students in " + selectedCourse.getCourseId()
                + " (" + selectedCourse.getEnrollmentCount() + " / " + selectedCourse.getCapacity() + ")");
        enrollmentTable.setItems(FXCollections.observableArrayList(
                context.getRegistrationService().getActiveStudentsForCourse(selectedCourse.getCourseId())));
        dropEnrollmentButton.setDisable(enrollmentTable.getItems().isEmpty());
    }

    private void populateForm(Course course) {
        selectedCourse = course;
        if (course == null) {
            clearForm();
            return;
        }
        courseIdField.setText(course.getCourseId());
        courseIdField.setDisable(true);
        titleField.setText(course.getTitle());
        creditsField.setText(String.valueOf(course.getCreditHours()));
        capacityField.setText(String.valueOf(course.getCapacity()));
        instructorField.setText(course.getInstructor());
        scheduleField.setText(course.getSchedule());
        formTitleLabel.setText("Editing " + course.getCourseId());
        addButton.setDisable(true);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
        showMessage("", false);
        refreshEnrollments();
    }

    private void clearForm() {
        selectedCourse = null;
        courseTable.getSelectionModel().clearSelection();
        courseIdField.clear();
        courseIdField.setDisable(false);
        titleField.clear();
        creditsField.clear();
        capacityField.clear();
        instructorField.clear();
        scheduleField.clear();
        formTitleLabel.setText("Add a new course");
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        showMessage("", false);
        refreshEnrollments();
    }

    @FXML
    private void handleAdd() {
        try {
            context.getCourseService().addCourse(
                    courseIdField.getText(),
                    titleField.getText(),
                    Validator.requireIntInRange(creditsField.getText(), "Credit hours", 1, 6),
                    Validator.requireIntInRange(capacityField.getText(), "Capacity", 1, 500),
                    instructorField.getText(),
                    scheduleField.getText());
            refreshTable(searchField.getText());
            clearForm();
            showMessage("Course added successfully.", false);
        } catch (RegistrationException ex) {
            showMessage(ex.getMessage(), true);
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCourse == null) {
            showMessage("Select a course from the table first.", true);
            return;
        }
        try {
            context.getCourseService().updateCourse(
                    selectedCourse.getCourseId(),
                    titleField.getText(),
                    Validator.requireIntInRange(creditsField.getText(), "Credit hours", 1, 6),
                    Validator.requireIntInRange(capacityField.getText(), "Capacity", 1, 500),
                    instructorField.getText(),
                    scheduleField.getText());
            String id = selectedCourse.getCourseId();
            refreshTable(searchField.getText());
            clearForm();
            showMessage("Course " + id + " updated.", false);
        } catch (RegistrationException ex) {
            showMessage(ex.getMessage(), true);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedCourse == null) {
            showMessage("Select a course from the table first.", true);
            return;
        }
        boolean confirmed = AlertHelper.confirm(window(), "Delete course",
                "Delete " + selectedCourse.getCourseId() + " - " + selectedCourse.getTitle() + "?");
        if (!confirmed) {
            return;
        }
        try {
            String id = selectedCourse.getCourseId();
            context.getRegistrationService().deleteCourse(id);
            refreshTable(searchField.getText());
            clearForm();
            showMessage("Course " + id + " deleted.", false);
        } catch (RegistrationException ex) {
            showMessage(ex.getMessage(), true);
        }
    }

    @FXML
    private void handleDropEnrollment() {
        Student student = enrollmentTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null || student == null) {
            showMessage("Select a course and a student first.", true);
            return;
        }
        boolean confirmed = AlertHelper.confirm(window(), "Drop enrollment",
                "Drop " + student.getName() + " from " + selectedCourse.getCourseId() + "?");
        if (!confirmed) {
            return;
        }
        try {
            context.getRegistrationService().drop(student.getId(), selectedCourse.getCourseId());
            String message = "Student " + student.getId() + " dropped from "
                    + selectedCourse.getCourseId() + ".";
            refreshEnrollments();
            refreshTable(searchField.getText());
            showMessage(message, false);
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
        showMessage("Showing " + courseTable.getItems().size() + " course(s).", false);
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
        return courseTable.getScene() == null ? null : courseTable.getScene().getWindow();
    }
}
