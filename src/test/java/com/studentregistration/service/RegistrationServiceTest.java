package com.studentregistration.service;

import com.studentregistration.exception.CourseFullException;
import com.studentregistration.exception.DuplicateEnrollmentException;
import com.studentregistration.exception.EntityNotFoundException;
import com.studentregistration.exception.ValidationException;
import com.studentregistration.model.Course;
import com.studentregistration.model.Registration;
import com.studentregistration.model.Student;
import com.studentregistration.repository.CourseRepository;
import com.studentregistration.repository.FileStorageManager;
import com.studentregistration.repository.RegistrationRepository;
import com.studentregistration.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the enrollment business rules in {@link RegistrationService}.
 */
class RegistrationServiceTest {

    @TempDir
    Path tempDirectory;

    private FileStorageManager storage;
    private StudentService students;
    private CourseService courses;
    private RegistrationService registrations;

    @BeforeEach
    void setUp() throws IOException {
        storage = new FileStorageManager(tempDirectory);
        students = new StudentService(new StudentRepository(storage));
        courses = new CourseService(new CourseRepository(storage));
        registrations = new RegistrationService(
                students, courses, new RegistrationRepository(storage));
    }

    private Student addStudent(String id) {
        return students.addStudent(id, "Test Student", id.toLowerCase() + "@taibah.edu.sa",
                "0500000000", "Computer Science", 1);
    }

    private Course addCourse(String id, int capacity) {
        return courses.addCourse(id, "Test Course", 3, capacity, "Dr. Test", "Sun 08:00");
    }

    @Test
    @DisplayName("enrolling creates an active registration and takes one seat")
    void enrollsStudent() {
        Student student = addStudent("STU-1");
        Course course = addCourse("CS113", 2);

        Registration registration = registrations.enroll(student.getId(), course.getCourseId());

        assertTrue(registration.isActive());
        assertEquals(1, course.getEnrollmentCount());
        assertEquals(1, course.getAvailableSeats());
        assertEquals(1, registrations.countActiveRegistrations());
        assertEquals(1, registrations.getActiveCoursesForStudent("STU-1").size());
    }

    @Test
    @DisplayName("a student cannot enroll twice in the same course")
    void rejectsDuplicateEnrollment() {
        Student student = addStudent("STU-1");
        Course course = addCourse("CS113", 5);
        registrations.enroll(student.getId(), course.getCourseId());

        assertThrows(DuplicateEnrollmentException.class,
                () -> registrations.enroll("stu-1", "cs113"));
        assertEquals(1, course.getEnrollmentCount());
    }

    @Test
    @DisplayName("a full course rejects further enrollments")
    void rejectsEnrollmentInFullCourse() {
        Student first = addStudent("STU-1");
        Student second = addStudent("STU-2");
        Course course = addCourse("CS113", 1);
        registrations.enroll(first.getId(), course.getCourseId());

        assertThrows(CourseFullException.class,
                () -> registrations.enroll(second.getId(), course.getCourseId()));
        assertEquals(1, course.getEnrollmentCount());
        assertEquals(0, course.getAvailableSeats());
    }

    @Test
    @DisplayName("enrolling an unknown student or course fails")
    void rejectsUnknownEntities() {
        Student student = addStudent("STU-1");
        Course course = addCourse("CS113", 5);

        assertThrows(EntityNotFoundException.class,
                () -> registrations.enroll("STU-404", course.getCourseId()));
        assertThrows(EntityNotFoundException.class,
                () -> registrations.enroll(student.getId(), "CS404"));
    }

    @Test
    @DisplayName("dropping frees the seat and keeps the registration history")
    void dropsCourse() {
        Student student = addStudent("STU-1");
        Course course = addCourse("CS113", 1);
        Registration registration = registrations.enroll(student.getId(), course.getCourseId());

        registrations.drop(student.getId(), course.getCourseId());

        assertFalse(registration.isActive());
        assertEquals(0, course.getEnrollmentCount());
        assertEquals(1, course.getAvailableSeats());
        assertEquals(0, registrations.countActiveRegistrations());
    }

    @Test
    @DisplayName("dropping a course the student is not enrolled in fails")
    void rejectsDropWithoutEnrollment() {
        addStudent("STU-1");
        addCourse("CS113", 5);

        assertThrows(EntityNotFoundException.class,
                () -> registrations.drop("STU-1", "CS113"));
    }

    @Test
    @DisplayName("active enrollments are re-attached after restarting the services")
    void restoresEnrollmentsFromCsv() {
        Student student = addStudent("STU-1");
        Course course = addCourse("CS113", 5);
        registrations.enroll(student.getId(), course.getCourseId());

        StudentService reloadedStudents = new StudentService(new StudentRepository(storage));
        CourseService reloadedCourses = new CourseService(new CourseRepository(storage));
        RegistrationService reloadedRegistrations = new RegistrationService(
                reloadedStudents, reloadedCourses, new RegistrationRepository(storage));

        Course reloadedCourse = reloadedCourses.findById("CS113");
        assertEquals(1, reloadedCourse.getEnrollmentCount());
        assertEquals(1, reloadedRegistrations.countActiveRegistrations());
        assertEquals(1, reloadedRegistrations.getActiveStudentsForCourse("CS113").size());
    }

    @Test
    @DisplayName("a student with active enrollments cannot be deleted")
    void blocksDeletionOfEnrolledStudent() {
        Student student = addStudent("STU-1");
        Course course = addCourse("CS113", 5);
        registrations.enroll(student.getId(), course.getCourseId());

        assertThrows(ValidationException.class,
                () -> registrations.deleteStudent(student.getId()));
        assertTrue(students.findOptional(student.getId()).isPresent());
    }

    @Test
    @DisplayName("a course with active enrollments cannot be deleted")
    void blocksDeletionOfEnrolledCourse() {
        Student student = addStudent("STU-1");
        Course course = addCourse("CS113", 5);
        registrations.enroll(student.getId(), course.getCourseId());

        assertThrows(ValidationException.class,
                () -> registrations.deleteCourse(course.getCourseId()));
        assertTrue(courses.findOptional(course.getCourseId()).isPresent());
    }

    @Test
    @DisplayName("a student can be deleted after dropping every course")
    void deletesStudentAfterDroppingCourses() {
        Student student = addStudent("STU-1");
        Course course = addCourse("CS113", 5);
        registrations.enroll(student.getId(), course.getCourseId());
        registrations.drop(student.getId(), course.getCourseId());

        registrations.deleteStudent(student.getId());

        assertTrue(students.findOptional("STU-1").isEmpty());
        assertEquals(0, registrations.countActiveRegistrations());
    }

    @Test
    @DisplayName("registrations of one student and one course are listed correctly")
    void listsEnrollments() {
        Student student = addStudent("STU-1");
        Course first = addCourse("CS113", 5);
        Course second = addCourse("CS210", 5);
        registrations.enroll(student.getId(), first.getCourseId());
        registrations.enroll(student.getId(), second.getCourseId());

        List<Course> enrolled = registrations.getActiveCoursesForStudent("STU-1");

        assertEquals(2, enrolled.size());
        assertEquals("CS113", enrolled.get(0).getCourseId());
        assertEquals(1, registrations.getActiveStudentsForCourse("CS113").size());
        assertEquals(1, registrations.getActiveStudentsForCourse("CS210").size());
    }
}
