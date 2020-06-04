package no.unit.nva.institution.proxy;

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
import static testutils.HttpClientGetsNestedInstitutionResponse.INSTITUTION_REQUEST_URI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import no.unit.nva.institution.proxy.exception.FailedHttpRequestException;
import no.unit.nva.institution.proxy.exception.HttpClientFailureException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.JsonParsingException;
import no.unit.nva.institution.proxy.exception.NonExistingUnitError;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.utils.InstitutionUtils;
import no.unit.nva.institution.proxy.utils.Language;
import nva.commons.utils.IoUtils;
import nva.commons.utils.JsonUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testutils.HttpClientGetsNestedInstitutionResponse;
import testutils.HttpClientReturningInfoOfSingleUnits;
import testutils.HttpClientThrowsExceptionInFirstRequestButSucceedsInSecond;

public class HttpExecutorImplTest {

    public static final String CRISTIN_RESPONSES_RES_FOLDER = "cristin_responses";
    public static final String HTTP_CLIENT_RESPONSES_RES_FOLDER = "httpClientResponses";
    public static final Path SINGLE_CRISTIN_USER_INSTITUTION = Path.of(CRISTIN_RESPONSES_RES_FOLDER,
        "one_institution_nb.json");
    public static final Path MANY_CRISTIN_USER_INSTITUTIONS = Path.of(CRISTIN_RESPONSES_RES_FOLDER,
        "all_institutions_nb.json");

    public static final Path SINGLE_UNIT_GRAPH = Path.of(HTTP_CLIENT_RESPONSES_RES_FOLDER,
        "singleUnitResponseGraph.json");

    private static final Path EXPECTED_NESTED_INSTITUTION_FOR_VALID_REQUEST = Path.of(
        CRISTIN_RESPONSES_RES_FOLDER, "expectedNestedInstitutionForValidRequest.json"
    );
    private static final String EMPTY_ARRAY = "[]";
    private static final String INVALID_JSON_STR = "Invalid json object";
    private static final String HTTP_ERROR_RESPONSE = "Http response error";

    public static final String WHITE_SPACE = "\\s";

    public static final String CRISTIN_RESPONSES = "cristin_responses";
    public static final String SINGLE_UNIT_RESPONSE = "unit_with_two_parents.json";
    public static final URI SAMPLE_URI = URI.create("https://api.cristin.no/v2/units/185.15.3.0");

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

        HttpClientFailureException exception = assertThrows(HttpClientFailureException.class,
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

        HttpClientFailureException exception = assertThrows(HttpClientFailureException.class,
            () -> executor.getInstitutions(Language.ENGLISH));

        Throwable cause = exception.getCause();
        assertThat(cause.getClass(), is(equalTo(IOException.class)));
        assertThat(exception.getStatusCode(), is(equalTo(HttpClientFailureException.ERROR_CODE)));
        assertThat(exception.getMessage(), containsString(INVALID_JSON_STR));
    }

    @DisplayName("getNestedInstitution returns nested institution when input is valid")
    @Test
    void getNestedInstitutionReturnsNestedInstitutionWhenUriAndLanguageAreValid()
        throws InvalidUriException, HttpClientFailureException, JsonParsingException, IOException {
        HttpClient client = new HttpClientGetsNestedInstitutionResponse(Language.ENGLISH).getMockClient();
        HttpExecutorImpl executor = new HttpExecutorImpl(client);
        JsonNode response = executor.getNestedInstitution(URI.create(INSTITUTION_REQUEST_URI),
            Language.ENGLISH);
        JsonNode expectedJson =
            JsonUtils.objectMapper.readTree(
                IoUtils.inputStreamFromResources(EXPECTED_NESTED_INSTITUTION_FOR_VALID_REQUEST));
        assertThat(response, is(equalTo(expectedJson)));
    }

    @DisplayName("getNestedInstitutions returns success when HttpClient throws exception in first attempt succeeds"
        + "in the second")
    @Test
    public void getNestedInstitutionsReturnsSuccessWhenHttpClientThrowsExceptionInFirstAttemptAndSucceedsInTheSecond()
        throws InterruptedException, ExecutionException, InvalidUriException, HttpClientFailureException, IOException {
        HttpClient client = new HttpClientThrowsExceptionInFirstRequestButSucceedsInSecond().getClient();
        HttpExecutorImpl executor = new HttpExecutorImpl(client);
        JsonNode response = executor.getNestedInstitution(URI.create(INSTITUTION_REQUEST_URI),
            Language.ENGLISH);

        JsonNode expectedJson =
            JsonUtils.objectMapper.readTree(
                IoUtils.inputStreamFromResources(EXPECTED_NESTED_INSTITUTION_FOR_VALID_REQUEST));
        assertThat(response, is(equalTo(expectedJson)));
    }

    @Test
    void getSingleUnitReturnsANestedInstitutionResponseWhenInputIsValid()
        throws InterruptedException, HttpClientFailureException, NonExistingUnitError,
               JsonParsingException, JsonProcessingException {
        HttpClient mockHttpClient = new HttpClientReturningInfoOfSingleUnits();
        HttpExecutorImpl executor = new HttpExecutorImpl(mockHttpClient);

        JsonNode actualResponse = executor.getSingleUnit(SAMPLE_URI, Language.ENGLISH);
        JsonNode expectedResponse = JsonUtils.objectMapper.readTree(IoUtils.stringFromResources(SINGLE_UNIT_GRAPH));
        assertThat(actualResponse, is(equalTo(expectedResponse)));
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
}
