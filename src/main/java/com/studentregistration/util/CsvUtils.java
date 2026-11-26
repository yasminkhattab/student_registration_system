package com.studentregistration.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Minimal RFC-4180-style CSV helpers.
 *
 * <p>Hand-written on purpose: the course teaches Java file APIs, so the project
 * does not depend on an external CSV library for such a small need.</p>
 */
public final class CsvUtils {

    private CsvUtils() {
        // Utility class: no instances.
    }

    /**
     * Joins values into one CSV line, quoting values that contain separators,
     * quotes or line breaks.
     *
     * @param values field values
     * @return a single CSV line (without line terminator)
     */
    public static String join(List<String> values) {
        return values.stream()
                .map(CsvUtils::escape)
                .collect(Collectors.joining(","));
    }

    /**
     * Splits a CSV line into fields, supporting quoted values with doubled
     * quotes.
     *
     * @param line raw CSV line
     * @return parsed fields
     */
    public static List<String> parse(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (insideQuotes) {
                if (character == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        insideQuotes = false;
                    }
                } else {
                    current.append(character);
                }
            } else if (character == '"') {
                insideQuotes = true;
            } else if (character == ',') {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        fields.add(current.toString());
        return fields;
    }

    private static String escape(String value) {
        String safe = value == null ? "" : value;
        boolean needsQuotes = safe.contains(",") || safe.contains("\"")
                || safe.contains("\n") || safe.contains("\r");
        if (!needsQuotes) {
            return safe;
        }
        return '"' + safe.replace("\"", "\"\"") + '"';
    }
}
