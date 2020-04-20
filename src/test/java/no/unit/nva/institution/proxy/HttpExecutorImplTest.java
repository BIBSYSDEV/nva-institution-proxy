package no.unit.nva.institution.proxy;


import java.util.HashMap;
import java.util.Map;
import no.unit.nva.institution.proxy.exception.FailedHttpRequestException;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.response.NestedInstitutionResponse;
import no.unit.nva.institution.proxy.utils.InstitutionUtils;
import no.unit.nva.institution.proxy.utils.Language;
import no.unit.nva.institution.proxy.utils.UriUtils;
import nva.commons.utils.IoUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpExecutorImplTest {

    public static final String CRISTIN_RESPONSES_RES_FOLDER = "cristin_responses";
    public static final Path EXAMPLE_INSTITUTION = Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_institution.json");
    public static final Path SINGLE_CRISTIN_USER_INSTITUTION = Path.of(CRISTIN_RESPONSES_RES_FOLDER,
        "one_institution_nb.json");
    public static final Path MANY_CRISTIN_USER_INSTITUTIONS = Path.of(CRISTIN_RESPONSES_RES_FOLDER,
        "all_institutions_nb.json");
    private static final String EMPTY_ARRAY = "[]";
    private static final String INVALID_JSON_STR = "Invalid json object";
    private static final String HTTP_ERROR_RESPONSE = "Http response error";
    public static final String INSTITUTION_REQUEST_URI =
        "https://api.cristin.no/v2/institutions/1";
    public static final String INSTITUTION_PARENT_REQUEST_URI =
        "https://api.cristin.no/v2/units?parent_unit_id=1.0.0.0&per_page=20000";
    private static final Path EXAMPLE_INSTITUTION_PARENT =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_parent_institution.json");
    private static final Path CORRESPONDING_UNIT =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_corresponding_unit.json");
    private static final Path ADMINISTRATION =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_administration.json");
    private static final Path ADMINISTRATION_ONE =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_administration_one.json");
    private static final Path ADMINISTRATION_TWO =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_administration_two.json");
    private static final Path CULTURAL_STUDIES =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_cultural_studies.json");
    private static final Path WELSH_LANGUAGE =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_welsh_language.json");
    private static final Path ADMINISTRATION_LH =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_administration_one_lh.json");

    private static final Path EXPECTED_NESTED_INSTITUTION_FOR_VALID_REQUEST = Path.of(
        CRISTIN_RESPONSES_RES_FOLDER, "expectedNestedInstitutionForValidRequest.json"
    );
    private static final String CORRESPONDING_UNIT_URI = "https://api.cristin.no/v2/units/1.0.0.0";
    private static final String ADMINISTRATION_URI = "https://api.cristin.no/v2/units/1.1.0.0";
    private static final String ADMINISTRATION_ONE_URI = "https://api.cristin.no/v2/units/1.1.1.0";
    private static final String ADMINISTRATION_TWO_URI = "https://api.cristin.no/v2/units/1.1.2.0";
    private static final String ADMINISTRATION_ONE_LH_URI = "https://api.cristin.no/v2/units/1.1.1.1";
    private static final String CULTURAL_STUDIES_URI = "https://api.cristin.no/v2/units/1.3.0.0";
    private static final String WELSH_LANGUAGE_URI = "https://api.cristin.no/v2/units/1.4.0.0";
    public static final String WHITE_SPACE = "\\s";

    /**
     * Setup tests.
     */

    @DisplayName("getInstitutions returns an InstitutionListResponse with one object when cristin response"
        + "contains one object ")
    @Test
    public void getInstitutionReturnsAnInstitutionListResponseWithOneObjectWhenCristinResponseContainsOneObject()
        throws Exception {
        String jsonBody = IoUtils.stringFromResources(SINGLE_CRISTIN_USER_INSTITUTION);
        HttpClient client = httpClientWithResponseBody(jsonBody);
        InstitutionListResponse expectedInstitution = InstitutionUtils.toInstitutionListResponse(jsonBody);

        HttpExecutorImpl executor = new HttpExecutorImpl(client);

        InstitutionListResponse actual = executor.getInstitutions(Language.ENGLISH);
        assertThat(actual, is(not(nullValue())));
        assertThat(actual.size(), is(greaterThan(0)));
        assertThat(actual, is(equalTo(expectedInstitution)));
    }

    @DisplayName("getInstitutions returns an InstitutionListResponse with many objects when cristin response"
        + "contains one object ")
    @Test
    public void getInstitutionReturnsAnInstitutionListResponseWithManyObjectsWhenCristinResponseContainsOneObject()
        throws Exception {
        String jsonBody = IoUtils.stringFromResources(MANY_CRISTIN_USER_INSTITUTIONS);
        HttpClient client = httpClientWithResponseBody(jsonBody);
        InstitutionListResponse expectedInstitution = InstitutionUtils.toInstitutionListResponse(jsonBody);

        HttpExecutorImpl executor = new HttpExecutorImpl(client);

        InstitutionListResponse actual = executor.getInstitutions(Language.ENGLISH);
        assertThat(actual, is(not(nullValue())));
        assertThat(actual.size(), is(greaterThan(1)));
        assertThat(actual, is(equalTo(expectedInstitution)));
    }

    @DisplayName("getInstitutions returns an empty list when cristin response contains no objects ")
    @Test
    public void getInstitutionReturnsAnEmptyListWhenCristinResponseContainsNoObjects()
        throws Exception {
        HttpClient client = httpClientWithResponseBody(EMPTY_ARRAY);
        InstitutionListResponse expectedInstitution = new InstitutionListResponse(Collections.emptyList());
        HttpExecutorImpl executor = new HttpExecutorImpl(client);
        InstitutionListResponse actual = executor.getInstitutions(Language.ENGLISH);
        assertThat(actual, is(not(nullValue())));
        assertThat(actual.size(), is(equalTo(0)));
        assertThat(actual, is(equalTo(expectedInstitution)));
    }

    @DisplayName("getInstitutions throws an GatewayException when client returns an error status code")
    @Test
    public void getInstitutionsThrowsAnInstitutionFailureExceptionWhenClientThrowsException() {

        HttpExecutorImpl executor = new HttpExecutorImpl(httpClientReturnsError());

        GatewayException exception = assertThrows(GatewayException.class,
            () -> executor.getInstitutions(Language.ENGLISH));

        Throwable cause = exception.getCause();
        assertThat(cause.getClass(), is(equalTo(FailedHttpRequestException.class)));
        assertThat(exception.getStatusCode(), is(equalTo(FailedHttpRequestException.ERROR_CODE)));
        assertThat(exception.getMessage(), containsString(HTTP_ERROR_RESPONSE));
    }

    @DisplayName("getInstitutions throws an GatewayException when parsing of response body fails")
    @Test
    public void getInstitutionsThrowsAnInstitutionFailureExceptionWhenParsingOfResponseFails() {

        HttpExecutorImpl executor = new HttpExecutorImpl(httpClientWithResponseBody(INVALID_JSON_STR));

        GatewayException exception = assertThrows(GatewayException.class,
            () -> executor.getInstitutions(Language.ENGLISH));

        Throwable cause = exception.getCause();
        assertThat(cause.getClass(), is(equalTo(IOException.class)));
        assertThat(exception.getStatusCode(), is(equalTo(GatewayException.ERROR_CODE)));
        assertThat(exception.getMessage(), containsString(INVALID_JSON_STR));
    }

    @DisplayName("getNestedInstitution returns nested institution when input is valid")
    @Test

    void getNestedInstitutionReturnsNestedInstitutionWhenUriAndLanguageAreValid()
        throws InvalidUriException, GatewayException {
        HttpClient client = new HttpClientGetsNestedInstitutionResponse(Language.ENGLISH).getMockClient();
        HttpExecutorImpl executor = new HttpExecutorImpl(client);
        NestedInstitutionResponse response = executor.getNestedInstitution(URI.create(INSTITUTION_REQUEST_URI),
            Language.ENGLISH);
        String expectedJson =
            IoUtils.stringFromResources(EXPECTED_NESTED_INSTITUTION_FOR_VALID_REQUEST);
        assertThat(removeWhiteSpaces(response.getJson()),
            is(equalTo(removeWhiteSpaces(expectedJson))));
    }




    private HttpClient httpClientReturnsError() {
        HttpClient client = mock(HttpClient.class);
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
        when(response.body()).thenReturn(HTTP_ERROR_RESPONSE);
        when(client.sendAsync(any(HttpRequest.class), any(BodyHandler.class)))
            .thenReturn(CompletableFuture.completedFuture(response));
        return client;
    }

    private HttpClient httpClientWithResponseBody(String responseBody) {
        HttpClient client = mock(HttpClient.class);
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(client.sendAsync(any(HttpRequest.class), any(BodyHandler.class)))
            .thenReturn(CompletableFuture.completedFuture(httpResponse));
        return client;
    }

    private String removeWhiteSpaces(String input) {
        return input.replaceAll(WHITE_SPACE, "");
    }

    private static class HttpClientGetsNestedInstitutionResponse {

        public static final String MOCK_ERROR = "MockHttp client setup failed for request:";
        private Map<URI, String> requestResponseMapping = new HashMap<>();

        public HttpClientGetsNestedInstitutionResponse(Language language) throws InvalidUriException {
            initializePathResponseMap(language);
        }

        private HttpClient getMockClient() {
            HttpClient client = mock(HttpClient.class);

            when(client.sendAsync(any(HttpRequest.class), any(BodyHandler.class)))
                .thenAnswer(new Answer<CompletableFuture<HttpResponse<String>>>() {
                    @Override
                    public CompletableFuture<HttpResponse<String>> answer(InvocationOnMock invocation) {
                        HttpRequest request = invocation.getArgument(0);
                        if (requestResponseMapping.containsKey(request.uri())) {
                            HttpResponse<String> response = createMockHttpResponse(request);
                            return CompletableFuture.completedFuture(response);
                        }
                        throw new IllegalStateException(MOCK_ERROR + request.uri().toString());
                    }
                });

            return client;
        }

        public HttpResponse<String> createMockHttpResponse(HttpRequest request) {
            HttpResponse<String> response = mock(HttpResponse.class);
            when(response.statusCode()).thenReturn(HttpStatus.SC_OK);
            when(response.body()).thenReturn(requestResponseMapping.get(request.uri()));
            return response;
        }

        private void initializePathResponseMap(Language language) throws InvalidUriException {
            requestResponseMapping.put(createUri(INSTITUTION_REQUEST_URI, language),
                IoUtils.stringFromResources(EXAMPLE_INSTITUTION));
            requestResponseMapping.put(createUri(INSTITUTION_PARENT_REQUEST_URI, language),
                IoUtils.stringFromResources(EXAMPLE_INSTITUTION_PARENT));
            requestResponseMapping.put(createUri(CORRESPONDING_UNIT_URI, language),
                IoUtils.stringFromResources(CORRESPONDING_UNIT));
            requestResponseMapping.put(createUri(ADMINISTRATION_URI, language),
                IoUtils.stringFromResources(ADMINISTRATION));
            requestResponseMapping.put(createUri(ADMINISTRATION_ONE_URI, language),
                IoUtils.stringFromResources(ADMINISTRATION_ONE));
            requestResponseMapping.put(createUri(ADMINISTRATION_TWO_URI, language),
                IoUtils.stringFromResources(ADMINISTRATION_TWO));
            requestResponseMapping.put(createUri(ADMINISTRATION_ONE_LH_URI, language),
                IoUtils.stringFromResources(ADMINISTRATION_LH));
            requestResponseMapping.put(createUri(CULTURAL_STUDIES_URI, language),
                IoUtils.stringFromResources(CULTURAL_STUDIES));
            requestResponseMapping.put(createUri(WELSH_LANGUAGE_URI, language),
                IoUtils.stringFromResources(WELSH_LANGUAGE));
        }

        private URI createUri(String uri, Language language) throws InvalidUriException {
            return UriUtils.getUriWithLanguage(URI.create(uri), language);
        }
    }
}
