package no.unit.nva.institution.proxy.utils;

import static nva.commons.utils.attempt.Try.attempt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import nva.commons.utils.attempt.Try;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LanguageTest {

    public static final String INVALID_LANG = "eng";

    @Test
    @DisplayName("getLanguage returns a Language for all valid value")
    public void getLanguageReturnsALanguageForAllValidValues() {
        List<String> validStrings = Arrays.stream(Language.values())
                                          .map(Language::getCode)
                                          .collect(Collectors.toList());
        List<Try<Language>> attempts = validStrings.stream()
                                                   .map(attempt(Language::getLanguage))
                                                   .collect(Collectors.toList());
        attempts.forEach(attempt -> {
            assertThat(attempt.isSuccess(), is(equalTo(true)));
            assertThat(attempt.get(), is(notNullValue()));
        });
    }

    @Test
    @DisplayName("getLanguage throws an UnknownLanguageException when the input is invalid")
    public void getLanguageThrowsAnUnknownLanguageExceptionWhenTheInputIsInvalid() {
        assertThrows(UnknownLanguageException.class, () -> Language.getLanguage(INVALID_LANG));
    }

    @Test
    @DisplayName("getLanguage returns the invalid lang string in the exception message when it throws an Exception")
    public void getLanguageRetunsTheInvalidLangStingInTheExceptionMessageWhenItThrowsAnException() {
        UnknownLanguageException exception = assertThrows(UnknownLanguageException.class,
            () -> Language.getLanguage(INVALID_LANG));
        assertThat(exception.getMessage(), containsString(INVALID_LANG));
    }

    @Test
    @DisplayName("getCode returns that language code of a Language")
    public void getCode() {
        String expectedEnglishCode = "en";
        assertThat(Language.ENGLISH.getCode(), is(equalTo(expectedEnglishCode)));
    }
}