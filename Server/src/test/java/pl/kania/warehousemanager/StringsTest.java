package pl.kania.warehousemanager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringsTest {

    @Test
    void givenNullableStringWhenCallingIsNullOrEmptyThenReturnTrue() {
        assertTrue(Strings.isNullOrEmpty(null));
    }

    @Test
    void givenNonBlankStringWhenCallingIsNullOrEmptyThenReturnFalse() {
        assertFalse(Strings.isNullOrEmpty("example"));
    }

    @ParameterizedTest(name = "Check if calling isNullOrEmpty() on string containing only whitespace ({0}) returns true")
    @EnumSource(value = Whitespace.class)
    void givenNonEmptyStringWithWhitespaceWhenCallingIsNullOrEmptyThenReturnTrue(Whitespace whitespace) {
        assertTrue(Strings.isNullOrEmpty(whitespace.getValue()));
    }

    @Getter
    @AllArgsConstructor
    enum Whitespace {
        EMPTY_STRING("", "Empty string"),
        SPACE(" ", "Space"),
        CARET_RETURN("\r", "\\r"),
        TAB("\t", "\\t"),
        NEW_LINE("\n", "\\n");

        private String value;
        private String description;

        @Override
        public String toString() {
            return description;
        }
    }
}