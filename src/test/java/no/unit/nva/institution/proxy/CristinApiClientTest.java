package no.unit.nva.institution.proxy;


import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.NonExistingUnitError;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.response.NestedInstitutionResponse;
import no.unit.nva.institution.proxy.utils.Language;
import no.unit.nva.institution.proxy.utils.LanguageMapper;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.utils.TestLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static nva.commons.utils.attempt.Try.attempt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class CristinApiClientTest {

    public static final String INVALID_LANGUAGE_CODE = "lalala";
    public static final String EMPTY_STRING = "";
    public static final String BLANK_STRING = "   ";
    public static final Language VALID_LANGUAGE_EN = Language.ENGLISH;
    public static final URI VALID_URI = URI.create("https://example.org");
    public static final String JSON_VALUE = "true";
    TestLogger testLogger = new TestLogger();

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

    @DisplayName("getNestedInstitution returns nested institution when input is valid")
    @Test
    void getNestedInstitutionReturnsNestedInstitutionWhenInputIsValid()
        throws InvalidUriException, GatewayException {
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
        attempt(() -> cristinApiClient.getInstitutions(Language.getLanguage(language)));
        String expectedLog = String.format(LanguageMapper.LOG_LANGUAGE_MAPPING_TEMPLATE, language);
        assertThat(testLogger.getLogs(), containsString(expectedLog));
    }

    private void getLanguageReturnNorwegianBokmalWhenLanguageIsUndefined(String languageString)
        throws ApiGatewayException {
        MockHttpExecutorReportingInsertedLanguage executor = new MockHttpExecutorReportingInsertedLanguage();

        CristinApiClient cristinApiClient = new CristinApiClient(executor, testLogger);
        cristinApiClient.getInstitutions(Language.getLanguage(languageString));
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

        @Override
        public NestedInstitutionResponse getSingleUnit(URI uri, Language language) throws InterruptedException, ExecutionException, InvalidUriException, NonExistingUnitError {
            this.insertedLanguage = language;
            return new NestedInstitutionResponse("true");
        }

        public Language getInsertedLanguage() {
            return insertedLanguage;
        }
    }
}