package com.studentregistration.controller;

import com.studentregistration.config.AppContext;
import com.studentregistration.model.Course;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Controller for the administrator dashboard ({@code admin-dashboard.fxml}).
 *
 * <p>Shows aggregated statistics and a read-only overview of all courses.</p>
 */
public class AdminDashboardController {

    @FXML private Label adminNameLabel;
    @FXML private Label studentCountLabel;
    @FXML private Label courseCountLabel;
    @FXML private Label enrollmentCountLabel;
    @FXML private Label seatUsageLabel;
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> courseIdColumn;
    @FXML private TableColumn<Course, String> titleColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, Integer> enrolledColumn;
    @FXML private TableColumn<Course, Integer> capacityColumn;

    private final AppContext context;

    /**
     * @param context shared application context
     */
    public AdminDashboardController(AppContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        adminNameLabel.setText(context.getCurrentAdministrator().getName() + " · "
                + context.getCurrentAdministrator().getDepartment());

        courseIdColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getCourseId()));
        titleColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getTitle()));
        instructorColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getInstructor()));
        enrolledColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getEnrollmentCount()).asObject());
        capacityColumn.setCellValueFactory(data ->
                new ReadOnlyIntegerWrapper(data.getValue().getCapacity()).asObject());

        courseTable.setPlaceholder(new Label("No courses yet. Open Course Management to add one."));
        refresh();
    }

    private void refresh() {
        int students = context.getStudentService().count();
        int courses = context.getCourseService().count();
        int enrollments = context.getRegistrationService().countActiveRegistrations();
        int capacity = context.getCourseService().getTotalCapacity();
        int usedSeats = context.getCourseService().getTotalEnrollment();
        int percent = capacity == 0 ? 0 : (int) Math.round(usedSeats * 100.0 / capacity);

        studentCountLabel.setText(String.valueOf(students));
        courseCountLabel.setText(String.valueOf(courses));
        enrollmentCountLabel.setText(String.valueOf(enrollments));
        seatUsageLabel.setText(usedSeats + " / " + capacity + " (" + percent + "%)");

        courseTable.setItems(FXCollections.observableArrayList(
                context.getCourseService().getAllCourses()));
    }

    @FXML
    private void handleRefresh() {
        refresh();
    }

    @FXML
    private void handleManageStudents() {
        context.getNavigator().showStudentManagement();
    }

    @FXML
    private void handleManageCourses() {
        context.getNavigator().showCourseManagement();
    }

    @FXML
    private void handleLogout() {
        context.logout();
        context.getNavigator().showLogin();
    }
}
