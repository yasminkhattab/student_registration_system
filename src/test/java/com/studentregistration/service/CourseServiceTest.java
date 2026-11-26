package com.studentregistration.service;

import com.studentregistration.exception.EntityNotFoundException;
import com.studentregistration.exception.ValidationException;
import com.studentregistration.model.Course;
import com.studentregistration.model.Student;
import com.studentregistration.repository.CourseRepository;
import com.studentregistration.repository.FileStorageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link CourseService}.
 */
class CourseServiceTest {

    @TempDir
    Path tempDirectory;

    private FileStorageManager storage;
    private CourseService courses;

    @BeforeEach
    void setUp() throws IOException {
        storage = new FileStorageManager(tempDirectory);
        courses = new CourseService(new CourseRepository(storage));
    }

    private Course addSample(String id, int capacity) {
        return courses.addCourse(id, "Programming II", 3, capacity,
                "Dr. Mona Al-Harbi", "Sun/Tue 10:00-11:30");
    }

    @Test
    @DisplayName("a valid course is stored with its capacity")
    void addsCourse() {
        addSample("CS113", 30);

        assertEquals(1, courses.count());
        assertEquals(30, courses.findById("CS113").getCapacity());
        assertEquals(30, courses.getTotalCapacity());
        assertEquals(0, courses.getTotalEnrollment());
    }

    @Test
    @DisplayName("duplicate course IDs are rejected")
    void rejectsDuplicateId() {
        addSample("CS113", 30);

        assertThrows(ValidationException.class, () -> addSample("cs113", 20));
    }

    @Test
    @DisplayName("credit hours outside 1-6 are rejected")
    void rejectsInvalidCreditHours() {
        assertThrows(ValidationException.class, () -> courses.addCourse(
                "CS999", "Invalid", 9, 10, "Dr. Test", "Sun 08:00"));
    }

    @Test
    @DisplayName("a capacity below 1 is rejected")
    void rejectsInvalidCapacity() {
        assertThrows(ValidationException.class, () -> addSample("CS998", 0));
    }

    @Test
    @DisplayName("a course can be updated")
    void updatesCourse() {
        addSample("CS113", 30);

        courses.updateCourse("CS113", "Programming II (Advanced)", 4, 40,
                "Dr. Huda Al-Amri", "Mon/Wed 08:00-09:30");

        Course updated = courses.findById("CS113");
        assertEquals("Programming II (Advanced)", updated.getTitle());
        assertEquals(4, updated.getCreditHours());
        assertEquals(40, updated.getCapacity());
    }

    @Test
    @DisplayName("capacity cannot be reduced below the number of enrolled students")
    void rejectsCapacityBelowEnrollment() {
        Course course = addSample("CS113", 5);
        Student first = new Student("STU-1", "Sara", "sara@taibah.edu.sa", "0500000000");
        Student second = new Student("STU-2", "Omar", "omar@taibah.edu.sa", "0500000001");
        course.enroll(first);
        course.enroll(second);

        assertThrows(ValidationException.class, () -> courses.updateCourse(
                "CS113", "Programming II", 3, 1, "Dr. Mona Al-Harbi", "Sun/Tue 10:00-11:30"));
    }

    @Test
    @DisplayName("a course can be deleted")
    void deletesCourse() {
        addSample("CS113", 30);

        courses.deleteCourse("CS113");

        assertEquals(0, courses.count());
        assertThrows(EntityNotFoundException.class, () -> courses.findById("CS113"));
    }

    @Test
    @DisplayName("search matches identifier, title and instructor")
    void searchesCourses() {
        addSample("CS113", 30);
        courses.addCourse("IS220", "Database Fundamentals", 3, 35,
                "Dr. Khalid Al-Saadi", "Mon/Wed 10:00-11:30");

        assertEquals(1, courses.search("programming").size());
        assertEquals(1, courses.search("saadi").size());
        assertEquals(1, courses.search("cs1").size());
    }

    @Test
    @DisplayName("courses are reloaded from the CSV file by a new service instance")
    void persistsAcrossInstances() {
        addSample("CS113", 30);

        CourseService reloaded = new CourseService(new CourseRepository(storage));

        assertEquals(1, reloaded.count());
        assertEquals("Dr. Mona Al-Harbi", reloaded.findById("CS113").getInstructor());
    }
}
