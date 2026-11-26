package com.studentregistration.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the low-level CSV storage.
 */
class FileStorageManagerTest {

    @TempDir
    Path tempDirectory;

    @Test
    @DisplayName("written lines are read back unchanged")
    void writesAndReadsLines() throws IOException {
        FileStorageManager storage = new FileStorageManager(tempDirectory);

        storage.writeLines("sample.csv", List.of("header", "value"));

        assertEquals(List.of("header", "value"), storage.readLines("sample.csv"));
        assertTrue(Files.exists(storage.resolve("sample.csv")));
    }

    @Test
    @DisplayName("a missing file is reported as empty")
    void missingFileIsEmpty() throws IOException {
        FileStorageManager storage = new FileStorageManager(tempDirectory);

        assertTrue(storage.readLines("missing.csv").isEmpty());
    }

    @Test
    @DisplayName("writing again replaces the previous content")
    void overwritesExistingFile() throws IOException {
        FileStorageManager storage = new FileStorageManager(tempDirectory);
        storage.writeLines("sample.csv", List.of("old"));

        storage.writeLines("sample.csv", List.of("new", "content"));

        assertEquals(List.of("new", "content"), storage.readLines("sample.csv"));
    }
}
