package no.unit.nva.institution.proxy.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnknownLanguageExceptionTest {

    public static final String THE_MESSAGE = "The message";

    @DisplayName("The exception can be thrown and has a message")
    @Test
    void unknownLanguageExceptionCanBeThrownAndHasMessage() {
        UnknownLanguageException exception = assertThrows(UnknownLanguageException.class, () -> {
            throw new UnknownLanguageException(THE_MESSAGE);
        });
        assertEquals(THE_MESSAGE, exception.getMessage());
    }
}