package com.studentregistration.repository;

import com.studentregistration.exception.DataAccessException;
import com.studentregistration.model.Student;
import com.studentregistration.util.CsvUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV persistence for students ({@code data/students.csv}).
 */
public class StudentRepository {

    private static final String FILE_NAME = "students.csv";
    private static final String HEADER = "id,name,email,phone,major,level";

    private final FileStorageManager storage;

    public StudentRepository(FileStorageManager storage) {
        this.storage = storage;
    }

    /**
     * Loads every student from disk.
     *
     * @return students in file order
     * @throws DataAccessException if the file cannot be read or is malformed
     */
    public List<Student> findAll() {
        try {
            List<Student> students = new ArrayList<>();
            for (String line : storage.readLines(FILE_NAME)) {
                if (line.isBlank() || line.startsWith("id,")) {
                    continue;
                }
                List<String> fields = CsvUtils.parse(line);
                if (fields.size() < 6) {
                    throw new DataAccessException("Malformed student row: " + line,
                            new IllegalArgumentException("expected 6 columns but found " + fields.size()));
                }
                try {
                    students.add(new Student(fields.get(0), fields.get(1), fields.get(2), fields.get(3),
                            fields.get(4), Integer.parseInt(fields.get(5))));
                } catch (IllegalArgumentException ex) {
                    throw new DataAccessException("Invalid student row: " + line, ex);
                }
            }
            return students;
        } catch (IOException ex) {
            throw new DataAccessException("Could not read " + FILE_NAME + ": " + ex.getMessage(), ex);
        }
    }

    /**
     * Rewrites the complete student file.
     *
     * @param students students to persist
     * @throws DataAccessException if the file cannot be written
     */
    public void saveAll(List<Student> students) {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Student student : students) {
            lines.add(toCsvLine(student));
        }
        try {
            storage.writeLines(FILE_NAME, lines);
        } catch (IOException ex) {
            throw new DataAccessException("Could not save " + FILE_NAME + ": " + ex.getMessage(), ex);
        }
    }

    /**
     * Formats one student as a CSV row.
     *
     * <p>Package-private on purpose: it is an implementation detail that is
     * visible to tests in the same package, but not to services or controllers.
     * This is one of the project's intentionally package-private methods.</p>
     *
     * @param student student to format
     * @return comma-separated row
     */
    String toCsvLine(Student student) {
        return CsvUtils.join(List.of(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getPhone(),
                student.getMajor(),
                String.valueOf(student.getLevel())));
    }
}
