package no.unit.nva.institution.proxy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.institution.proxy.CristinApiClient;
import no.unit.nva.institution.proxy.exception.FailedHttpRequestException;
import no.unit.nva.institution.proxy.exception.HttpClientFailureException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.MissingParameterException;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.utils.Language;
import nva.commons.apigateway.GatewayResponse;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import nva.commons.core.JsonUtils;
import nva.commons.core.attempt.Failure;
import nva.commons.core.attempt.Try;
import nva.commons.core.ioutils.IoUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static no.unit.nva.institution.proxy.handler.NestedInstitutionHandler.URI_QUERY_PARAMETER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NestedInstitutionHandlerTest extends HandlerTest {

    private static final String SOME_ENV_VALUE = "SOME_VALUE";
    public static final String EVENTS_FOLDER = "events";
    private static final Path NESTED_INSTITUTIONS_REQUEST_WITH_VALID_QUERY_PARAMETERS =
        Path.of(EVENTS_FOLDER, "nested_institutions_request_with_valid_parameters.json");
    public static final Path NESTED_INSTITUTIONS_REQUEST_WITH_INVALID_URL_JSON = Path.of(
        EVENTS_FOLDER, "nested_institutions_request_with_invalid_url.json");
    public static final Path NESTED_INSTITUTIONS_REQUEST_WITH_MALFORMED_URL_JSON = Path.of(
        EVENTS_FOLDER, "nested_institutions_request_with_malformed_url.json");

    public static final Path NESTED_INSTITUTIONS_REQUEST_WITH_INVALID_LANGUAGE = Path.of(EVENTS_FOLDER,
        "nested_institutions_request_with_invalid_language.json");
    public static final Path NESTED_INSTITUTIONS_REQUEST_WITH_MISSING_PARAMETERS_OBJECT = Path.of(EVENTS_FOLDER,
        "nested_institutions_request_with_no_parameters_object.json");

    public static final Path NESTED_INSTITUTIONS_REQUEST_WITH_NO_LANGUAGE_PARAMETER = Path.of(EVENTS_FOLDER,
        "nested_institutions_request_with_no_language_parameter.json");

    public static final String LANGUAGE_STRING_VALUE_IN_RESOURCE_FILE = "en";
    public static final String URI_STRING_VALUE_IN_RESOURCE_FILE = "http://get-info.no/institutions";

    private static final String SOME_EXCEPTION_MESSAGE = "THIS EXCEPTION";
    public static final ObjectMapper objectMapper = JsonUtils.dtoObjectMapper;
    public static final Context CONTEXT = mock(Context.class);

    private Environment environment;
    private Context context;

    /**
     * Setup.
     */
    @BeforeEach
    public void setup() {
        environment = mock(Environment.class);
        when(environment.readEnv(anyString())).thenReturn(SOME_ENV_VALUE);
        context = CONTEXT;
    }

    @DisplayName("Tests an entire institution can be nested from live data")
    @Test
    @Tag("online")
    void testAnInstitutionCanBeNestedFromLiveData() throws IOException {
        NestedInstitutionHandler nestedInstitutionHandler =
            new NestedInstitutionHandler(environment, new CristinApiClient());
        InputStream inputStream =
            IoUtils.inputStreamFromResources(Path.of("events", "nested_institutions_request_for_uio.json"));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        nestedInstitutionHandler.handleRequest(inputStream, outputStream, CONTEXT);
        JsonNode response = extractResponseObjectFromOutputStream(outputStream);
        assertThat(true, is(true));
    }

    @DisplayName("Tests that a single unit can be nested from live data")
    @Test
    @Tag("online")
    void testSingleUnitCanBeNestedFromLiveData() throws Exception {

        NestedInstitutionHandler nestedInstitutionHandler =
            new NestedInstitutionHandler(environment, new CristinApiClient());

        InputStream input = Try
            .of(Path.of(EVENTS_FOLDER, "nested_institutions_request_for_single_unit_uio.json"))
            .map(IoUtils::inputStreamFromResources)
            .orElseThrow(Failure::getException);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        nestedInstitutionHandler.handleRequest(input, outputStream, context);
        JsonNode response = extractResponseObjectFromOutputStream(outputStream);
    }

    private JsonNode extractResponseObjectFromOutputStream(ByteArrayOutputStream outputStream)
        throws com.fasterxml.jackson.core.JsonProcessingException {
        TypeReference<GatewayResponse<JsonNode>> tr = new TypeReference<>() {};
        return objectMapper
            .readValue(outputStream.toString(StandardCharsets.UTF_8), tr)
            .getBodyObject(JsonNode.class);
    }

    @DisplayName("The NestedInstitutionHandler exists")
    @Test
    void nestedInstitutionHandlerExists() {
        new NestedInstitutionHandler(environment, new CristinApiClient());
    }

    @DisplayName("method processInput can receive correctly formatted request")
    @Test
    void processInputReceivesCorrectInput() throws IOException {
        NestedInstitutionHandlerWithGetRequest handler = nestedInstitutionHandlerWithAccessibleRequestInfo();
        InputStream inputStream = inputInstitutionsRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(inputStream, outputStream, context);
        String actualLanguageParameter = handler.getRequestinfo()
            .getQueryParameters()
            .get(NestedInstitutionHandler.LANGUAGE_QUERY_PARAMETER);
        assertThat(actualLanguageParameter,
            is(equalTo(LANGUAGE_STRING_VALUE_IN_RESOURCE_FILE)));
        String actualUriParameter = handler.getRequestinfo()
            .getQueryParameters()
            .get(URI_QUERY_PARAMETER);
        assertThat(actualUriParameter, is(equalTo(URI_STRING_VALUE_IN_RESOURCE_FILE)));
    }

    @DisplayName("handleRequest returns OK to the client when language parameter is missing")
    @Test
    public void handleRequestReturnsOkToTheClientWhenTheLanguageParameterIsMissing() throws IOException {
        NestedInstitutionHandlerWithGetRequest handler = nestedInstitutionHandlerWithAccessibleRequestInfo();

        InputStream inputStream = inputLanguageParameterMissing();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        String outputString = outputStream.toString(StandardCharsets.UTF_8);
        GatewayResponse<InstitutionListResponse> response = objectMapper.readValue(outputString, GatewayResponse.class);
        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @DisplayName("handleRequest returns OK to the client when the processing is successful")
    @Test
    public void handleRequestReturnsOkToTheClientWhenTheProcessingIsSuccessful() throws IOException {
        NestedInstitutionHandlerWithGetRequest handler = nestedInstitutionHandlerWithAccessibleRequestInfo();

        InputStream inputStream = inputInstitutionsRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        String outputString = outputStream.toString(StandardCharsets.UTF_8);
        GatewayResponse<InstitutionListResponse> response = objectMapper.readValue(outputString, GatewayResponse.class);
        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @DisplayName("handleRequest returns BadRequest to the client when UnknownLanguageException occurs")
    @Test
    public void handleRequestReturnsBadRequestWhenUnknownLanguageExceptionOccurs()
        throws IOException {
        NestedInstitutionHandler handler = handlerWithNonFunctionalCritinClient();
        InputStream inputStream = invalidLanguageRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBodyObject(Problem.class).getDetail(),
            containsString(INVALID_LANGUAGE_IN_RESOURCES_FILE));
    }

    @DisplayName("handleRequest returns BadRequest to the client when InvalidUriException occurs")
    @Test
    public void handleRequestReturnsBadRequestWhenInvalidUriExceptionOccurs()
        throws IOException {

        NestedInstitutionHandlerWithGetRequest handler = nestedInstitutionHandlerWithAccessibleRequestInfo();
        InputStream inputStream = inputInvalidRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);
        String expectedStringInErrorMessage = handler.getRequestinfo().getQueryParameters().get(URI_QUERY_PARAMETER);

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBodyObject(Problem.class).getDetail(), containsString(expectedStringInErrorMessage));
    }

    @DisplayName("handleRequest returns BadRequest to the client when input URI is malformed")
    @Test
    public void handleRequestReturnsBadRequestWhenInputUriIsMalformated()
        throws IOException {

        NestedInstitutionHandlerWithGetRequest handler = nestedInstitutionHandlerWithAccessibleRequestInfo();
        InputStream inputStream = inputMalformedUrl();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);
        String expectedStringInErrorMessage = handler.getRequestinfo().getQueryParameters().get(URI_QUERY_PARAMETER);

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBodyObject(Problem.class).getDetail(), containsString(expectedStringInErrorMessage));
    }

    @DisplayName("handleRequest returns BadRequest to the client when URL parameter is missing")
    @Test
    public void handleRequestReturnsBadRequestWhenUriParameterIsMissing()
        throws IOException {

        NestedInstitutionHandlerWithGetRequest handler = nestedInstitutionHandlerWithAccessibleRequestInfo();
        InputStream inputStream = inputNoParametersObject();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);
        String expectedStringInErrorMessage = MissingParameterException.MESSAGE_PATTERN + URI_QUERY_PARAMETER;

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBodyObject(Problem.class).getDetail(), containsString(expectedStringInErrorMessage));
    }

    @DisplayName("handleRequest returns BadGateway to the client when GatewayException occurs")
    @Test
    public void handleRequestReturnsBadRequestWhenInstitutionFailureOccurs()
        throws IOException, InvalidUriException, HttpClientFailureException, FailedHttpRequestException {
        NestedInstitutionHandler handler = handlerThatThrowsNestedInstitutionFailureException(SOME_EXCEPTION_MESSAGE);
        InputStream inputStream = inputInstitutionsRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_GATEWAY));
        assertThat(response.getBodyObject(Problem.class).getDetail(), containsString(SOME_EXCEPTION_MESSAGE));
    }

    private NestedInstitutionHandler handlerThatThrowsNestedInstitutionFailureException(String message)
        throws InvalidUriException, HttpClientFailureException, FailedHttpRequestException {
        CristinApiClient cristinClient = mock(CristinApiClient.class);
        IOException cause = new IOException(message);
        when(cristinClient.getNestedInstitution(any(URI.class), any(Language.class)))
            .thenThrow(new HttpClientFailureException(cause));
        return new NestedInstitutionHandler(environment, cristinClient);
    }

    private GatewayResponse<Problem> gatewayResponseWithProblem(ByteArrayOutputStream outputStream)
        throws IOException {
        String outputString = outputStream.toString(StandardCharsets.UTF_8);
        TypeReference<GatewayResponse<Problem>> ref = new TypeReference<>() {
        };
        return objectMapper.readValue(outputString, ref);
    }

    private NestedInstitutionHandler handlerWithNonFunctionalCritinClient() {
        CristinApiClient cristinClient = mock(CristinApiClient.class);
        return new NestedInstitutionHandler(environment, cristinClient);
    }

    private InputStream inputInstitutionsRequest() {
        String input = IoUtils.stringFromResources(NESTED_INSTITUTIONS_REQUEST_WITH_VALID_QUERY_PARAMETERS);
        return IoUtils.stringToStream(input);
    }

    private InputStream inputLanguageParameterMissing() {
        String input = IoUtils.stringFromResources(NESTED_INSTITUTIONS_REQUEST_WITH_NO_LANGUAGE_PARAMETER);
        return IoUtils.stringToStream(input);
    }

    private InputStream invalidLanguageRequest() {
        String input = IoUtils.stringFromResources(NESTED_INSTITUTIONS_REQUEST_WITH_INVALID_LANGUAGE);
        return IoUtils.stringToStream(input);
    }

    private InputStream inputInvalidRequest() {
        String input = IoUtils.stringFromResources(NESTED_INSTITUTIONS_REQUEST_WITH_INVALID_URL_JSON);
        return IoUtils.stringToStream(input);
    }

    private InputStream inputMalformedUrl() {
        return IoUtils.inputStreamFromResources(NESTED_INSTITUTIONS_REQUEST_WITH_MALFORMED_URL_JSON);
    }

    private InputStream inputNoParametersObject() {
        return IoUtils.inputStreamFromResources(NESTED_INSTITUTIONS_REQUEST_WITH_MISSING_PARAMETERS_OBJECT);
    }

    private NestedInstitutionHandlerWithGetRequest nestedInstitutionHandlerWithAccessibleRequestInfo() {
        MockCristinApiClient cristinApiClient = new MockCristinApiClient();
        return new NestedInstitutionHandlerWithGetRequest(environment, cristinApiClient);
    }

    private static class NestedInstitutionHandlerWithGetRequest extends NestedInstitutionHandler {

        private RequestInfo requestInfo;

        public NestedInstitutionHandlerWithGetRequest(Environment environment, CristinApiClient cristinApiClient) {
            super(environment, cristinApiClient);
        }

        @Override
        protected JsonNode processInput(Void input, RequestInfo requestInfo,
                                        Context context)
            throws ApiGatewayException {
            this.requestInfo = requestInfo;
            return super.processInput(input, requestInfo, context);
        }

        public RequestInfo getRequestinfo() {
            return requestInfo;
        }
    }

    private static class MockCristinApiClient extends CristinApiClient {

        protected MockCristinApiClient() {
            super();
        }

        @Override
        public JsonNode getNestedInstitution(URI uri, Language language) {
            JsonNode jsonNode = objectMapper.createObjectNode();
            return jsonNode;
        }
    }
}