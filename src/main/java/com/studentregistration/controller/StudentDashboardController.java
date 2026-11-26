package com.studentregistration.controller;

import com.studentregistration.config.AppContext;
import com.studentregistration.model.Course;
import com.studentregistration.model.Student;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Student home screen ({@code student-dashboard.fxml}): personal information and
 * a summary of the current registrations.
 */
public class StudentDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label studentNameLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label majorLabel;
    @FXML private Label levelLabel;
    @FXML private Label enrolledCountLabel;
    @FXML private Label creditTotalLabel;

    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, String> courseIdColumn;
    @FXML private TableColumn<Course, String> titleColumn;
    @FXML private TableColumn<Course, Integer> creditsColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, String> scheduleColumn;

    private final AppContext context;

    /**
     * @param context shared application context
     */
    public StudentDashboardController(AppContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        Student student = context.getCurrentStudent();

        welcomeLabel.setText("Welcome, " + student.getName().split(" ")[0] + "!");
        studentNameLabel.setText(student.getName());
        studentIdLabel.setText(student.getId());
        emailLabel.setText(student.getEmail());
        phoneLabel.setText(student.getPhone());
        majorLabel.setText(student.getMajor());
        levelLabel.setText(String.valueOf(student.getLevel()));

        courseIdColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCourseId()));
        titleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTitle()));
        creditsColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getCreditHours()).asObject());
        instructorColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getInstructor()));
        scheduleColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getSchedule()));

        coursesTable.setPlaceholder(new Label("You are not enrolled in any course yet."));
        refreshCourses();
    }

    private void refreshCourses() {
        List<Course> courses = context.getRegistrationService()
                .getActiveCoursesForStudent(context.getCurrentStudent().getId());
        coursesTable.setItems(FXCollections.observableArrayList(courses));
        enrolledCountLabel.setText(String.valueOf(courses.size()));
        creditTotalLabel.setText(String.valueOf(
                courses.stream().mapToInt(Course::getCreditHours).sum()));
    }

    @FXML
    private void handleRefresh() {
        refreshCourses();
    }

    @FXML
    private void handleBrowseCourses() {
        context.getNavigator().showCourseCatalog();
    }

    @FXML
    private void handleMyCourses() {
        context.getNavigator().showMyCourses();
    }

    @FXML
    private void handleLogout() {
        context.logout();
        context.getNavigator().showLogin();
    }
}
