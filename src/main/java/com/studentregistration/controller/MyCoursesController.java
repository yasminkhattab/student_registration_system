package com.studentregistration.controller;

import com.studentregistration.config.AppContext;
import com.studentregistration.exception.RegistrationException;
import com.studentregistration.model.Course;
import com.studentregistration.util.AlertHelper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Student screen listing current registrations and allowing drops
 * ({@code my-courses.fxml}).
 */
public class MyCoursesController {

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> courseIdColumn;
    @FXML private TableColumn<Course, String> titleColumn;
    @FXML private TableColumn<Course, Integer> creditsColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, String> scheduleColumn;
    @FXML private Label totalCreditsLabel;
    @FXML private Label messageLabel;
    @FXML private Button dropButton;

    private final AppContext context;

    /**
     * @param context shared application context
     */
    public MyCoursesController(AppContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        courseIdColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCourseId()));
        titleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTitle()));
        creditsColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getCreditHours()).asObject());
        instructorColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getInstructor()));
        scheduleColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getSchedule()));

        courseTable.setPlaceholder(new Label("You are not enrolled in any course."));
        courseTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> dropButton.setDisable(newValue == null));

        dropButton.setDisable(true);
        refresh();
    }

    private void refresh() {
        var courses = context.getRegistrationService()
                .getActiveCoursesForStudent(context.getCurrentStudent().getId());
        courseTable.setItems(FXCollections.observableArrayList(courses));
        totalCreditsLabel.setText(String.valueOf(
                courses.stream().mapToInt(Course::getCreditHours).sum()));
    }

    @FXML
    private void handleDrop() {
        Course course = courseTable.getSelectionModel().getSelectedItem();
        if (course == null) {
            showMessage("Select a course to drop.", true);
            return;
        }
        boolean confirmed = AlertHelper.confirm(
                courseTable.getScene() == null ? null : courseTable.getScene().getWindow(),
                "Drop course",
                "Drop " + course.getCourseId() + " - " + course.getTitle() + "?");
        if (!confirmed) {
            return;
        }
        try {
            context.getRegistrationService().drop(
                    context.getCurrentStudent().getId(), course.getCourseId());
            refresh();
            showMessage("Dropped " + course.getCourseId() + ".", false);
        } catch (RegistrationException ex) {
            showMessage(ex.getMessage(), true);
        }
    }

    @FXML
    private void handleRefresh() {
        refresh();
        showMessage("", false);
    }

    @FXML
    private void handleBrowseCourses() {
        context.getNavigator().showCourseCatalog();
    }

    @FXML
    private void handleBack() {
        context.getNavigator().showStudentDashboard();
    }

    private void showMessage(String message, boolean error) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("status-label",
                error ? "status-error" : "status-success");
    }
}
