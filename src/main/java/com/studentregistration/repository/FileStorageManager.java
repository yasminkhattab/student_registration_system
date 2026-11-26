package com.studentregistration.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Low-level file access used by every repository.
 *
 * <p>All files live in one data directory (default {@code ./data}). Writes go
 * through a temporary file followed by an atomic move, so a crash cannot leave a
 * half-written CSV file behind.</p>
 */
public class FileStorageManager {

    private final Path dataDirectory;

    /**
     * Creates the storage manager and ensures the data directory exists.
     *
     * @param dataDirectory directory that holds all CSV files
     * @throws IOException if the directory cannot be created
     */
    public FileStorageManager(Path dataDirectory) throws IOException {
        this.dataDirectory = Objects.requireNonNull(dataDirectory, "dataDirectory")
                .toAbsolutePath()
                .normalize();
        Files.createDirectories(this.dataDirectory);
    }

    /**
     * Resolves a storage file inside the data directory.
     *
     * @param fileName file name such as {@code students.csv}
     * @return absolute path of the file
     */
    public Path resolve(String fileName) {
        return dataDirectory.resolve(fileName);
    }

    /**
     * Reads every line of a file. A missing file is treated as empty, which is
     * what a first run needs.
     *
     * @param fileName file to read
     * @return immutable list of lines
     * @throws IOException if the file exists but cannot be read
     */
    public List<String> readLines(String fileName) throws IOException {
        Path file = resolve(fileName);
        if (!Files.exists(file)) {
            return List.of();
        }
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines.toList();
        }
    }

    /**
     * Replaces the content of a file atomically.
     *
     * @param fileName file to write
     * @param lines    complete new content
     * @throws IOException if the file cannot be written
     */
    public void writeLines(String fileName, List<String> lines) throws IOException {
        Path target = resolve(fileName);
        Path temporary = Files.createTempFile(dataDirectory, fileName, ".tmp");
        try {
            Files.write(temporary, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            try {
                Files.move(temporary, target,
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }
}
