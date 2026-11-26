package com.studentregistration.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the no-argument and parameterized constructors required by the CS113
 * project brief.
 */
class EntityConstructorsTest {

    @Test
    @DisplayName("a no-argument student can be populated through setters")
    void noArgStudentUsesSetters() {
        Student student = new Student();
        student.setId("STU-7001");
        student.setName("Demo Student");
        student.setEmail("demo@taibah.edu.sa");
        student.setPhone("0500000000");
        student.setMajor("Computer Science");
        student.setLevel(4);

        assertEquals("STU-7001", student.getId());
        assertEquals("Computer Science", student.getMajor());
        assertEquals(4, student.getLevel());
        assertEquals("Student", student.getRole());
    }

    @Test
    @DisplayName("a no-argument administrator can be populated through setters")
    void noArgAdministratorUsesSetters() {
        Administrator administrator = new Administrator();
        administrator.setId("ADM-900");
        administrator.setName("Demo Admin");
        administrator.setEmail("admin@taibah.edu.sa");
        administrator.setPhone("0500000001");
        administrator.setDepartment("Registration");

        assertEquals("ADM-900", administrator.getId());
        assertEquals("Registration", administrator.getDepartment());
        assertEquals("Administrator", administrator.getRole());
    }

    @Test
    @DisplayName("a no-argument course starts empty but usable")
    void noArgCourseHasDefaults() {
        Course course = new Course();
        course.setCourseId("CS999");
        course.setTitle("Demo Course");
        course.setInstructor("Dr. Demo");

        assertEquals("CS999", course.getCourseId());
        assertEquals(30, course.getCapacity());
        assertEquals(30, course.getAvailableSeats());
        assertTrue(course.getEnrolledStudents().isEmpty());
    }

    @Test
    @DisplayName("a no-argument registration has a default active status")
    void noArgRegistrationIsActive() {
        Registration registration = new Registration();

        assertTrue(registration.isActive());
        assertEquals(RegistrationStatus.ACTIVE, registration.getStatus());
    }
}
