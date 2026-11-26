package com.studentregistration.repository;

import com.studentregistration.model.Course;
import com.studentregistration.model.Registration;
import com.studentregistration.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the CSV serialisation, including the package-private row formatters
 * that are visible to tests in this package only.
 */
class RepositoryCsvFormatTest {

    @TempDir
    Path tempDirectory;

    private FileStorageManager storage;

    @BeforeEach
    void setUp() throws IOException {
        storage = new FileStorageManager(tempDirectory);
    }

    @Test
    @DisplayName("a student row uses the documented column order and escapes commas")
    void formatsStudentRow() {
        StudentRepository repository = new StudentRepository(storage);
        Student student = new Student("STU-1", "Al-Harbi, Sara", "sara@taibah.edu.sa",
                "0500000000", "Computer Science", 2);

        assertEquals("STU-1,\"Al-Harbi, Sara\",sara@taibah.edu.sa,0500000000,Computer Science,2",
                repository.toCsvLine(student));
    }

    @Test
    @DisplayName("a course row uses the documented column order")
    void formatsCourseRow() {
        CourseRepository repository = new CourseRepository(storage);
        Course course = new Course("CS113", "Programming II", 3, 30,
                "Dr. Mona Al-Harbi", "Sun/Tue 10:00-11:30");

        assertEquals("CS113,Programming II,3,30,Dr. Mona Al-Harbi,Sun/Tue 10:00-11:30",
                repository.toCsvLine(course));
    }

    @Test
    @DisplayName("a registration row stores the ISO date and status name")
    void formatsRegistrationRow() {
        RegistrationRepository repository = new RegistrationRepository(storage);
        Registration registration = Registration.create("STU-1", "CS113");

        String row = repository.toCsvLine(registration);

        assertTrue(row.startsWith("REG-"));
        assertTrue(row.contains(",STU-1,CS113,"));
        assertTrue(row.endsWith(",ACTIVE"));
    }

    @Test
    @DisplayName("saved files start with the documented header row")
    void writesHeaders() throws IOException {
        StudentRepository students = new StudentRepository(storage);
        students.saveAll(List.of(new Student("STU-1", "Sara", "sara@taibah.edu.sa",
                "0500000000", "Computer Science", 1)));

        assertEquals("id,name,email,phone,major,level",
                storage.readLines("students.csv").get(0));
    }
}
