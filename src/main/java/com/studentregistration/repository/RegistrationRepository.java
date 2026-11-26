package com.studentregistration.repository;

import com.studentregistration.exception.DataAccessException;
import com.studentregistration.model.Registration;
import com.studentregistration.model.RegistrationStatus;
import com.studentregistration.util.CsvUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV persistence for registration records ({@code data/registrations.csv}).
 *
 * <p>Dropped courses keep their row with status {@code DROPPED}, giving the
 * project a small audit trail without a database.</p>
 */
public class RegistrationRepository {

    private static final String FILE_NAME = "registrations.csv";
    private static final String HEADER = "id,studentId,courseId,registeredAt,status";

    private final FileStorageManager storage;

    public RegistrationRepository(FileStorageManager storage) {
        this.storage = storage;
    }

    /**
     * Loads every registration record.
     *
     * @return registrations in file order
     * @throws DataAccessException if the file cannot be read or is malformed
     */
    public List<Registration> findAll() {
        try {
            List<Registration> registrations = new ArrayList<>();
            for (String line : storage.readLines(FILE_NAME)) {
                if (line.isBlank() || line.startsWith("id,")) {
                    continue;
                }
                List<String> fields = CsvUtils.parse(line);
                if (fields.size() < 5) {
                    throw new DataAccessException("Malformed registration row: " + line,
                            new IllegalArgumentException("expected 5 columns but found " + fields.size()));
                }
                try {
                    registrations.add(new Registration(
                            fields.get(0),
                            fields.get(1),
                            fields.get(2),
                            LocalDateTime.parse(fields.get(3)),
                            RegistrationStatus.valueOf(fields.get(4))));
                } catch (DateTimeParseException | IllegalArgumentException ex) {
                    throw new DataAccessException("Invalid registration row: " + line, ex);
                }
            }
            return registrations;
        } catch (IOException ex) {
            throw new DataAccessException("Could not read " + FILE_NAME + ": " + ex.getMessage(), ex);
        }
    }

    /**
     * Rewrites the complete registration file.
     *
     * @param registrations registrations to persist
     * @throws DataAccessException if the file cannot be written
     */
    public void saveAll(List<Registration> registrations) {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Registration registration : registrations) {
            lines.add(toCsvLine(registration));
        }
        try {
            storage.writeLines(FILE_NAME, lines);
        } catch (IOException ex) {
            throw new DataAccessException("Could not save " + FILE_NAME + ": " + ex.getMessage(), ex);
        }
    }

    /**
     * Formats one registration as a CSV row.
     *
     * @param registration registration to format
     * @return comma-separated row
     */
    String toCsvLine(Registration registration) {
        return CsvUtils.join(List.of(
                registration.getRegistrationId(),
                registration.getStudentId(),
                registration.getCourseId(),
                registration.getRegisteredAt().toString(),
                registration.getStatus().name()));
    }
}
