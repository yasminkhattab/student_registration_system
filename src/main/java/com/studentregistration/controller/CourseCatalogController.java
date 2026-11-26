package com.studentregistration.controller;

import com.studentregistration.config.AppContext;
import com.studentregistration.exception.RegistrationException;
import com.studentregistration.model.Course;
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
 * Student screen for browsing the catalogue and enrolling
 * ({@code course-catalog.fxml}).
 */
public class CourseCatalogController {

    @FXML private TextField searchField;
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> courseIdColumn;
    @FXML private TableColumn<Course, String> titleColumn;
    @FXML private TableColumn<Course, Integer> creditsColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, String> scheduleColumn;
    @FXML private TableColumn<Course, String> seatsColumn;
    @FXML private TableColumn<Course, String> statusColumn;
    @FXML private Label messageLabel;
    @FXML private Button enrollButton;

    private final AppContext context;

    /**
     * @param context shared application context
     */
    public CourseCatalogController(AppContext context) {
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
        seatsColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                String.valueOf(data.getValue().getAvailableSeats())));
        statusColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().isFull() ? "Full" : "Open"));

        courseTable.setPlaceholder(new Label("No courses match the current search."));
        courseTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    enrollButton.setDisable(newValue == null || newValue.isFull());
                    if (newValue != null && newValue.isFull()) {
                        showMessage("This course is full. Choose another course.", true);
                    } else {
                        showMessage("", false);
                    }
                });

        refreshTable(null);
    }

    private void refreshTable(String keyword) {
        courseTable.setItems(FXCollections.observableArrayList(
                context.getCourseService().search(keyword)));
    }

    @FXML
    private void handleEnroll() {
        Course course = courseTable.getSelectionModel().getSelectedItem();
        if (course == null) {
            showMessage("Select a course to enroll.", true);
            return;
        }
        try {
            context.getRegistrationService().enroll(
                    context.getCurrentStudent().getId(), course.getCourseId());
            refreshTable(searchField.getText());
            showMessage("Enrolled in " + course.getCourseId() + " - " + course.getTitle() + ".", false);
        } catch (RegistrationException ex) {
            showMessage(ex.getMessage(), true);
        }
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
    private void handleRefresh() {
        refreshTable(searchField.getText());
    }

    @FXML
    private void handleMyCourses() {
        context.getNavigator().showMyCourses();
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
