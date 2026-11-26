package com.studentregistration.repository;

import com.studentregistration.exception.DataAccessException;
import com.studentregistration.model.Course;
import com.studentregistration.util.CsvUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV persistence for courses ({@code data/courses.csv}).
 *
 * <p>Enrollments are not stored in this file; they live in
 * {@code registrations.csv} so that each fact has exactly one home.</p>
 */
public class CourseRepository {

    private static final String FILE_NAME = "courses.csv";
    private static final String HEADER = "id,title,creditHours,capacity,instructor,schedule";

    private final FileStorageManager storage;

    public CourseRepository(FileStorageManager storage) {
        this.storage = storage;
    }

    /**
     * Loads every course from disk. Enrolled students are re-attached later by
     * the registration service.
     *
     * @return courses in file order
     * @throws DataAccessException if the file cannot be read or is malformed
     */
    public List<Course> findAll() {
        try {
            List<Course> courses = new ArrayList<>();
            for (String line : storage.readLines(FILE_NAME)) {
                if (line.isBlank() || line.startsWith("id,")) {
                    continue;
                }
                List<String> fields = CsvUtils.parse(line);
                if (fields.size() < 6) {
                    throw new DataAccessException("Malformed course row: " + line,
                            new IllegalArgumentException("expected 6 columns but found " + fields.size()));
                }
                try {
                    courses.add(new Course(fields.get(0), fields.get(1),
                            Integer.parseInt(fields.get(2)), Integer.parseInt(fields.get(3)),
                            fields.get(4), fields.get(5)));
                } catch (IllegalArgumentException ex) {
                    throw new DataAccessException("Invalid course row: " + line, ex);
                }
            }
            return courses;
        } catch (IOException ex) {
            throw new DataAccessException("Could not read " + FILE_NAME + ": " + ex.getMessage(), ex);
        }
    }

    /**
     * Rewrites the complete course file.
     *
     * @param courses courses to persist
     * @throws DataAccessException if the file cannot be written
     */
    public void saveAll(List<Course> courses) {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Course course : courses) {
            lines.add(toCsvLine(course));
        }
        try {
            storage.writeLines(FILE_NAME, lines);
        } catch (IOException ex) {
            throw new DataAccessException("Could not save " + FILE_NAME + ": " + ex.getMessage(), ex);
        }
    }

    /**
     * Formats one course as a CSV row.
     *
     * @param course course to format
     * @return comma-separated row
     */
    String toCsvLine(Course course) {
        return CsvUtils.join(List.of(
                course.getCourseId(),
                course.getTitle(),
                String.valueOf(course.getCreditHours()),
                String.valueOf(course.getCapacity()),
                course.getInstructor(),
                course.getSchedule()));
    }
}
