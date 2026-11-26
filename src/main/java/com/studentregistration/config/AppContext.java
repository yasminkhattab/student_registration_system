package com.studentregistration.config;

import com.studentregistration.model.Administrator;
import com.studentregistration.model.Person;
import com.studentregistration.model.Student;
import com.studentregistration.repository.CourseRepository;
import com.studentregistration.repository.FileStorageManager;
import com.studentregistration.repository.RegistrationRepository;
import com.studentregistration.repository.StudentRepository;
import com.studentregistration.service.CourseService;
import com.studentregistration.service.RegistrationService;
import com.studentregistration.service.StudentService;
import com.studentregistration.util.SceneNavigator;

import java.io.IOException;

/**
 * Wires the application together and holds the session state.
 *
 * <p>This is the project's small "composition root": repositories are created
 * first, services receive them through constructors, and the JavaFX controllers
 * receive this context. No dependency-injection framework is needed.</p>
 */
public class AppContext {

    private final StudentService studentService;
    private final CourseService courseService;
    private final RegistrationService registrationService;
    private Person currentUser;
    private SceneNavigator navigator;

    private AppContext(StudentService studentService,
                       CourseService courseService,
                       RegistrationService registrationService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.registrationService = registrationService;
    }

    /**
     * Creates the storage, repositories and services, then seeds demo data on a
     * completely empty installation.
     *
     * @return a ready-to-use application context
     * @throws IOException when the data directory cannot be created
     */
    public static AppContext bootstrap() throws IOException {
        FileStorageManager storage = new FileStorageManager(AppConfig.DATA_DIRECTORY);

        StudentService students = new StudentService(new StudentRepository(storage));
        CourseService courses = new CourseService(new CourseRepository(storage));
        RegistrationService registrations = new RegistrationService(
                students, courses, new RegistrationRepository(storage));

        SampleDataInitializer.seedIfEmpty(students, courses, registrations);

        return new AppContext(students, courses, registrations);
    }

    public StudentService getStudentService() {
        return studentService;
    }

    public CourseService getCourseService() {
        return courseService;
    }

    public RegistrationService getRegistrationService() {
        return registrationService;
    }

    /**
     * @return the signed-in student
     * @throws IllegalStateException when the current user is not a student
     */
    public Student getCurrentStudent() {
        if (currentUser instanceof Student student) {
            return student;
        }
        throw new IllegalStateException("No student is signed in.");
    }

    /**
     * @return the signed-in administrator
     * @throws IllegalStateException when the current user is not an administrator
     */
    public Administrator getCurrentAdministrator() {
        if (currentUser instanceof Administrator administrator) {
            return administrator;
        }
        throw new IllegalStateException("No administrator is signed in.");
    }

    /**
     * @return the signed-in person, or {@code null} after logout
     */
    public Person getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Person currentUser) {
        this.currentUser = currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    /**
     * Stores the navigator after the primary stage exists. Controllers then use
     * {@link #getNavigator()} to switch screens.
     *
     * @param navigator screen navigator
     */
    public void setNavigator(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    /**
     * @return the screen navigator
     * @throws IllegalStateException when the application has not been fully started
     */
    public SceneNavigator getNavigator() {
        if (navigator == null) {
            throw new IllegalStateException("The screen navigator is not available yet.");
        }
        return navigator;
    }
}
