package com.studentregistration.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the hand-written CSV helpers.
 */
class CsvUtilsTest {

    @Test
    @DisplayName("plain values are joined with commas")
    void joinsPlainValues() {
        assertEquals("a,b,c", CsvUtils.join(List.of("a", "b", "c")));
    }

    @Test
    @DisplayName("values containing commas are quoted")
    void quotesValuesWithCommas() {
        assertEquals("\"Al-Harbi, Mona\",CS113",
                CsvUtils.join(List.of("Al-Harbi, Mona", "CS113")));
    }

    @Test
    @DisplayName("quoted values are parsed back correctly")
    void parsesQuotedValues() {
        assertEquals(List.of("Al-Harbi, Mona", "CS113"),
                CsvUtils.parse("\"Al-Harbi, Mona\",CS113"));
    }

    @Test
    @DisplayName("doubled quotes survive a round trip")
    void roundTripsQuotes() {
        String line = CsvUtils.join(List.of("The \"best\" course", "CS113"));
        assertEquals(List.of("The \"best\" course", "CS113"), CsvUtils.parse(line));
    }

    @Test
    @DisplayName("empty fields are preserved")
    void keepsEmptyFields() {
        assertEquals(List.of("STU-1", "", "CS113"), CsvUtils.parse("STU-1,,CS113"));
    }
}
