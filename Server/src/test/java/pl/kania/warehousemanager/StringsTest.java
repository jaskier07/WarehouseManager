package pl.kania.warehousemanager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringsTest {

    private static final String[] WHITESPACES = {" ", "\n", "\r", "\t", ""};

    @Test
    void givenNullableStringWhenCallingIsNullOrEmptyThenReturnTrue() {
        assertTrue(Strings.isNullOrEmpty(null));
    }

    @Test
    void givenNonBlankStringWhenCallingIsNullOrEmptyThenReturnFalse() {
        assertFalse(Strings.isNullOrEmpty("example"));
    }

    @Test
    @DisplayName("Check if calling isNullOrEmpty() on string containing only whitespaces returns true")
    void givenNonEmptyStringWithWhitespaceWhenCallingIsNullOrEmptyThenReturnTrue() {
        Arrays.stream(WHITESPACES).forEach(w -> assertTrue(Strings.isNullOrEmpty(w)));
    }
}