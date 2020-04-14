package no.unit.nva.institution.proxy;

import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.utils.Language;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.utils.TestLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;

import static nva.commons.utils.attempt.Try.attempt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CristinApiClientTest {

    public static final String INVALID_LANGUAGE_CODE = "lalala";
    public static final String EMPTY_STRING = "";
    public static final String BLANK_STRING = "   ";
    public static final String INVALID_URI = "not valid dot org";
    public static final String VALID_LANGUAGE_EN = "en";
    public static final String VALID_URI = "https://example.org";
    public static final String JSON_VALUE = "true";
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

    @DisplayName("getLanguage returns Norwegian Bokmål when language is blans")
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

    @DisplayName("getNestedInstitution throws unknown language exception when language is unknown")
    @Test
    void getNestedInstitutionThrowsUnknownLanguageExceptionWhenLanguageIsUnknown() {
        CristinApiClient cristinApiClient = new CristinApiClient(testLogger);
        UnknownLanguageException exception = assertThrows(UnknownLanguageException.class,
                () -> cristinApiClient.getNestedInstitution("https://example.org", INVALID_LANGUAGE_CODE));
        assertThat(exception.getMessage(), containsString(INVALID_LANGUAGE_CODE));
        assertThat(exception.getMessage(), containsString(Language.LANGUAGES_STRING));
    }

    @DisplayName("getNestedInstitution throws InvalidUriException when URI is invalid")
    @Test
    void getNestedInstitutionThrowsInvalidUriExceptionWhenUriIsInvalid() {
        CristinApiClient cristinApiClient = new CristinApiClient(testLogger);
        InvalidUriException exception = assertThrows(InvalidUriException.class,
                () -> cristinApiClient.getNestedInstitution(INVALID_URI, VALID_LANGUAGE_EN));
        assertThat(exception.getMessage(), containsString(INVALID_URI));
        String expectedLog = String.format(CristinApiClient.LOG_URI_ERROR_TEMPLATE, INVALID_URI);
        assertThat(testLogger.getLogs(), containsString(expectedLog));
    }

    @DisplayName("getNestedInstitution returns nested institution when input is valid")
    @Test
    void getNestedInstitutionReturnsNestedInstitutionWhenInputIsValid() throws InvalidUriException, GatewayException, UnknownLanguageException {
        HttpExecutor mockHttpExecutor = mock(HttpExecutorImpl.class);
        when(mockHttpExecutor.getNestedInstitution(any(), any()))
                .thenReturn(new NestedInstitutionResponse(JSON_VALUE));
        CristinApiClient cristinApiClient = new CristinApiClient(mockHttpExecutor, testLogger);
        NestedInstitutionResponse response = cristinApiClient.getNestedInstitution(VALID_URI, VALID_LANGUAGE_EN);
        assertThat(response.getJson(), containsString(JSON_VALUE));
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

        @Override
        public NestedInstitutionResponse getNestedInstitution(URI uri, Language language) {
            this.insertedLanguage = language;
            return new NestedInstitutionResponse("true");
        }

        public Language getInsertedLanguage() {
            return insertedLanguage;
        }
    }
}