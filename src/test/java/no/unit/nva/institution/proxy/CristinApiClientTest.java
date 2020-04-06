package no.unit.nva.institution.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.utils.Language;
import nva.commons.utils.TestLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CristinApiClientTest {

    public static final String INVALID_LANGUAGE_CODE = "lalala";
    TestLogger testLogger = new TestLogger();

    @DisplayName("getLanguage throws UnknownLanguageException for invalid language")
    @Test
    public void getLanguageThrowsUnknownLanguageExceptionForInvalidLanguage() {
        CristinApiClient cristinApiClient = new CristinApiClient(testLogger);
        UnknownLanguageException exception = assertThrows(UnknownLanguageException.class,
            () -> cristinApiClient.getInstitutions(INVALID_LANGUAGE_CODE));
        assertThat(exception.getMessage(), containsString(INVALID_LANGUAGE_CODE));
        assertThat(exception.getMessage(), containsString(Language.LANGUAGES_STRING));
    }
}