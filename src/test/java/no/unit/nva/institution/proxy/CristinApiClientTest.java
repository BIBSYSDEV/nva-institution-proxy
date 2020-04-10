package no.unit.nva.institution.proxy;

import static nva.commons.utils.attempt.Try.attempt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.utils.Language;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.utils.TestLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CristinApiClientTest {

    public static final String INVALID_LANGUAGE_CODE = "lalala";
    public static final String EMPTY_STRING = "";
    public static final String BLANK_STRING = "   ";
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

    @DisplayName("getLanguage returns Norwegian Bokmål when language is null")
    @Test
    public void getLanguageReturnsNorwegianBokmalWhenLanguageIsNull() throws ApiGatewayException {
        getLanguageReturnNorwegianBokmalWhenLanguageIsUndefined(null);
    }

    @DisplayName("getLanguage returns Norwegian Bokmål when language is blank")
    @Test
    public void getLanguageReturnsNorwegianBokmalWhenLanguageIsEmpty() throws ApiGatewayException {
        getLanguageReturnNorwegianBokmalWhenLanguageIsUndefined(EMPTY_STRING);
    }

    @DisplayName("getLanguage returns Norwegian Bokmål when language is empty")
    @Test
    public void getLanguageReturnsNorwegianBokmalWhenLanguageIsBlank() throws ApiGatewayException {
        getLanguageReturnNorwegianBokmalWhenLanguageIsUndefined(BLANK_STRING);
    }

    @DisplayName("getLanguage logs the input language when the language is valid")
    @Test
    public void getLanguageLogsTheInputLanguageWhenTheLanguageIsValid() {
        getLanguageLogsTheInputLanguage(Language.NORWEGIAN_BOKMAAL.getCode());
    }

    @DisplayName("getLanguage logs the input language when the language is invalid")
    @Test
    public void getLanguageLogsTheInputLanguageWhenTheLanguageIsInvalid() {
        getLanguageLogsTheInputLanguage(INVALID_LANGUAGE_CODE);
    }

    private void getLanguageLogsTheInputLanguage(String language) {
        MockHttpExecutorReportingInsertedLanguage executor = new MockHttpExecutorReportingInsertedLanguage();
        CristinApiClient cristinApiClient = new CristinApiClient(executor, testLogger);
        attempt(() -> cristinApiClient.getInstitutions(language));
        String expectedLog = String.format(CristinApiClient.LOG_LANGUAGE_MAPPING_TEMPLATE, language);
        assertThat(testLogger.getLogs(), containsString(expectedLog));
    }

    private void getLanguageReturnNorwegianBokmalWhenLanguageIsUndefined(String languageString)
        throws ApiGatewayException {
        MockHttpExecutorReportingInsertedLanguage executor = new MockHttpExecutorReportingInsertedLanguage();

        CristinApiClient cristinApiClient = new CristinApiClient(executor, testLogger);
        cristinApiClient.getInstitutions(languageString);
        assertThat(executor.getInsertedLanguage(), is(equalTo(Language.NORWEGIAN_BOKMAAL)));
    }

    private static class MockHttpExecutorReportingInsertedLanguage extends HttpExecutor {

        private Language insertedLanguage;

        @Override
        public InstitutionListResponse getInstitutions(Language language) {
            this.insertedLanguage = language;
            return new InstitutionListResponse(Collections.emptyList());
        }

        public Language getInsertedLanguage() {
            return insertedLanguage;
        }
    }
}