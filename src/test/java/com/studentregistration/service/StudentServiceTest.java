package com.studentregistration.service;

import com.studentregistration.exception.EntityNotFoundException;
import com.studentregistration.exception.ValidationException;
import com.studentregistration.model.Student;
import com.studentregistration.repository.FileStorageManager;
import com.studentregistration.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link StudentService}.
 */
class StudentServiceTest {

    @TempDir
    Path tempDirectory;

    private FileStorageManager storage;
    private StudentService students;

    @BeforeEach
    void setUp() throws IOException {
        storage = new FileStorageManager(tempDirectory);
        students = new StudentService(new StudentRepository(storage));
    }

    private Student addSample(String id) {
        return students.addStudent(id, "Sara Al-Qahtani", "sara@taibah.edu.sa",
                "0501234567", "Computer Science", 2);
    }

    @Test
    @DisplayName("a valid student is stored and can be found")
    void addsStudent() {
        addSample("STU-1001");

        assertEquals(1, students.count());
        assertEquals("Sara Al-Qahtani", students.findById("STU-1001").getName());
    }

    @Test
    @DisplayName("student IDs are normalised to upper case")
    void normalisesId() {
        students.addStudent("stu-1002", "Omar", "omar@taibah.edu.sa",
                "0501234568", "Computer Science", 3);

        assertTrue(students.findOptional("STU-1002").isPresent());
    }

    @Test
    @DisplayName("duplicate student IDs are rejected regardless of case")
    void rejectsDuplicateId() {
        addSample("STU-1001");

        assertThrows(ValidationException.class, () -> addSample("stu-1001"));
        assertEquals(1, students.count());
    }

    @Test
    @DisplayName("an invalid e-mail address is rejected")
    void rejectsInvalidEmail() {
        assertThrows(ValidationException.class, () -> students.addStudent(
                "STU-1003", "Layla", "not-an-email", "0501234569", "IS", 1));
        assertEquals(0, students.count());
    }

    @Test
    @DisplayName("an academic level outside 1-8 is rejected")
    void rejectsInvalidLevel() {
        assertThrows(ValidationException.class, () -> students.addStudent(
                "STU-1004", "Noura", "noura@taibah.edu.sa", "0501234570", "CS", 9));
    }

    @Test
    @DisplayName("an existing student can be updated")
    void updatesStudent() {
        addSample("STU-1001");

        students.updateStudent("STU-1001", "Sara A. Al-Qahtani", "sara.q@taibah.edu.sa",
                "0550000000", "Software Engineering", 3);

        Student updated = students.findById("STU-1001");
        assertEquals("Sara A. Al-Qahtani", updated.getName());
        assertEquals(3, updated.getLevel());
        assertEquals("Software Engineering", updated.getMajor());
    }

    @Test
    @DisplayName("a student can be deleted")
    void deletesStudent() {
        addSample("STU-1001");

        students.deleteStudent("STU-1001");

        assertEquals(0, students.count());
        assertThrows(EntityNotFoundException.class, () -> students.findById("STU-1001"));
    }

    @Test
    @DisplayName("search matches name, major and identifier")
    void searchesStudents() {
        addSample("STU-1001");
        students.addStudent("STU-2001", "Omar Al-Zahrani", "omar@taibah.edu.sa",
                "0501234568", "Information Systems", 1);

        assertEquals(1, students.search("sara").size());
        assertEquals(1, students.search("information").size());
        assertEquals(2, students.search("stu-").size());
        assertEquals(2, students.search("   ").size());
    }

    @Test
    @DisplayName("students are reloaded from the CSV file by a new service instance")
    void persistsAcrossInstances() {
        addSample("STU-1001");

        StudentService reloaded = new StudentService(new StudentRepository(storage));

        assertEquals(1, reloaded.count());
        assertEquals("sara@taibah.edu.sa", reloaded.findById("STU-1001").getEmail());
    }
}
