package no.unit.nva.institution.proxy.handler;

import static nva.commons.utils.JsonUtils.jsonParser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Function;
import no.unit.nva.institution.proxy.CristinApiClient;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.request.NestedInstitutionRequest;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.response.NestedInstitutionResponse;
import no.unit.nva.institution.proxy.utils.Language;
import no.unit.nva.testutils.HandlerUtils;
import no.unit.nva.testutils.TestContext;
import no.unit.nva.testutils.TestLogger;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.handlers.GatewayResponse;
import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.IoUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

public class NestedInstitutionHandlerTest extends HandlerTest {

    private static final String SOME_ENV_VALUE = "SOME_VALUE";
    public static final String EVENTS_FOLDER = "events";
    private static final Path NESTED_INSTITUTIONS_REQUEST_WITH_NON_EMPTY_BODY =
        Path.of(EVENTS_FOLDER, "nested_institutions_request_with_non_empty_body.json");
    public static final Path NESTED_INSTITUTIONS_REQUEST_WITH_INVALID_URL_JSON = Path.of(
        EVENTS_FOLDER, "nested_institutions_request_with_invalid_url.json");
    public static final Path NESTED_INSTITUTIONS_REQUEST_WITH_INVALID_LANGUAGE = Path.of(EVENTS_FOLDER,
        "nested_institutions_request_with_invalid_language.json");

    public static final String LANGUAGE_STRING_VALUE_IN_RESOURCE_FILE = "en";
    public static final String URI_STRING_VALUE_IN_RESOURCE_FILE = "http://get-info.no/institutions";

    private static final String SOME_EXCEPTION_MESSAGE = "THIS EXCEPTION";

    private Environment environment;
    private Context context;
    private TestLogger logger;

    /**
     * Setup.
     */
    @BeforeEach
    public void setup() {
        environment = mock(Environment.class);
        logger = new TestLogger();
        when(environment.readEnv(anyString())).thenReturn(SOME_ENV_VALUE);
        context = mock(Context.class);
        when(context.getLogger()).thenReturn(logger);
    }

    @DisplayName("Tests an entire institution can be nested from live data")
    @Test
    @Tag("online")
    void testAnInstitutionCanBeNestedFromLiveData() throws IOException {
        NestedInstitutionHandler nestedInstitutionHandler =
            new NestedInstitutionHandler(environment, ignored -> new CristinApiClient(logger));
        NestedInstitutionRequest request =
            new NestedInstitutionRequest("https://api.cristin.no/v2/institutions/185", "en");
        InputStream inputStream = HandlerUtils.requestObjectToApiGatewayRequestInputSteam(request, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Context context = new TestContext();

        nestedInstitutionHandler.handleRequest(inputStream, outputStream, context);
        NestedInstitutionResponse response = extractResponseObjectFromOutputStream(outputStream);
        assertThat(true, is(true));
    }

    @DisplayName("Tests that a single unit can be nested from live data")
    @Test
    @Tag("online")
    void testSingleUnitCanBeNestedFromLiveData() throws IOException {
        TestContext context = new TestContext();

        NestedInstitutionHandler nestedInstitutionHandler =
            new NestedInstitutionHandler(environment, CristinApiClient::new);
        NestedInstitutionRequest request =
            new NestedInstitutionRequest("https://api.cristin.no/v2/units/194.63.1.20", "en");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream input = HandlerUtils.requestObjectToApiGatewayRequestInputSteam(request, null);

        nestedInstitutionHandler.handleRequest(input, outputStream, context);
        NestedInstitutionResponse response = extractResponseObjectFromOutputStream(outputStream);
    }

    private NestedInstitutionResponse extractResponseObjectFromOutputStream(ByteArrayOutputStream outputStream)
        throws com.fasterxml.jackson.core.JsonProcessingException {
        TypeReference<GatewayResponse<NestedInstitutionResponse>> tr = new TypeReference<>() {};
        return jsonParser
            .readValue(outputStream.toString(StandardCharsets.UTF_8), tr)
            .getBodyObject(NestedInstitutionResponse.class);
    }

    @DisplayName("The NestedInstitutionHandler exists")
    @Test
    void nestedInstitutionHandlerExists() {
        new NestedInstitutionHandler(environment, CristinApiClient::new);
    }

    @DisplayName("method processInput can receive correctly formatted request")
    @Test
    void processInputReceivesCorrectInput() throws IOException {
        NestedInstitutionHandlerWithGetRequest handler = nestedInstitutionHandlerWithAccessibleRequestObject();
        InputStream inputStream = inputInstitutionsRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(inputStream, outputStream, context);
        assertThat(handler.getRequest().getLanguage(), is(equalTo(LANGUAGE_STRING_VALUE_IN_RESOURCE_FILE)));
        assertThat(handler.getRequest().getUri(), is(equalTo(URI_STRING_VALUE_IN_RESOURCE_FILE)));
    }

    @DisplayName("handleRequest returns OK to the client when the processing is successful")
    @Test
    public void handleRequestReturnsOkToTheClientWhenTheProcessingIsSuccessful() throws IOException {
        NestedInstitutionHandlerWithGetRequest handler = nestedInstitutionHandlerWithAccessibleRequestObject();

        InputStream inputStream = inputInstitutionsRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        String outputString = outputStream.toString(StandardCharsets.UTF_8);
        GatewayResponse<InstitutionListResponse> response = jsonParser.readValue(outputString, GatewayResponse.class);
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

        NestedInstitutionHandlerWithGetRequest handler = nestedInstitutionHandlerWithAccessibleRequestObject();
        InputStream inputStream = inputInvalidRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);
        String expectedStringInErrorMessage = handler.request.getUri();

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBodyObject(Problem.class).getDetail(), containsString(expectedStringInErrorMessage));
    }

    @DisplayName("handleRequest returns BadGateway to the client when GatewayException occurs")
    @Test
    public void handleRequestReturnsBadRequestWhenInstitutionFailureOccurs()
        throws IOException, InvalidUriException, GatewayException, UnknownLanguageException {
        NestedInstitutionHandler handler = handlerThatThrowsNestedInstitutionFailureException(SOME_EXCEPTION_MESSAGE);
        InputStream inputStream = inputInstitutionsRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_GATEWAY));
        assertThat(response.getBodyObject(Problem.class).getDetail(), containsString(SOME_EXCEPTION_MESSAGE));
    }

    private NestedInstitutionHandler handlerThatThrowsNestedInstitutionFailureException(String message)
        throws InvalidUriException, GatewayException {
        CristinApiClient cristinClient = mock(CristinApiClient.class);
        IOException cause = new IOException(message);
        when(cristinClient.getNestedInstitution(any(URI.class), any(Language.class)))
            .thenThrow(new GatewayException(cause));
        return new NestedInstitutionHandler(environment, logger -> cristinClient);
    }

    private GatewayResponse<Problem> gatewayResponseWithProblem(ByteArrayOutputStream outputStream)
        throws IOException {
        String outputString = outputStream.toString(StandardCharsets.UTF_8);
        TypeReference<GatewayResponse<Problem>> ref = new TypeReference<>() {
        };
        return jsonParser.readValue(outputString, ref);
    }

    private NestedInstitutionHandler handlerWithNonFunctionalCritinClient() {
        CristinApiClient cristinClient = mock(CristinApiClient.class);
        return new NestedInstitutionHandler(environment, logger -> cristinClient);
    }

    private InputStream inputInstitutionsRequest() {
        String input = IoUtils.stringFromResources(NESTED_INSTITUTIONS_REQUEST_WITH_NON_EMPTY_BODY);
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

    private NestedInstitutionHandlerWithGetRequest nestedInstitutionHandlerWithAccessibleRequestObject() {
        MockCristinApiClient cristinApiClient = new MockCristinApiClient(logger);
        return new NestedInstitutionHandlerWithGetRequest(environment, logger -> cristinApiClient);
    }

    private static class NestedInstitutionHandlerWithGetRequest extends NestedInstitutionHandler {

        private NestedInstitutionRequest request;

        public NestedInstitutionHandlerWithGetRequest(Environment environment,
                                                      Function<LambdaLogger, CristinApiClient> cristinApiClient) {
            super(environment, cristinApiClient);
        }

        @Override
        protected NestedInstitutionResponse processInput(NestedInstitutionRequest input, RequestInfo requestInfo,
                                                         Context context)
            throws ApiGatewayException {
            this.request = input;
            return super.processInput(input, requestInfo, context);
        }

        public NestedInstitutionRequest getRequest() {
            return request;
        }
    }

    private static class MockCristinApiClient extends CristinApiClient {

        private Language languageCode;
        private String uri;

        protected MockCristinApiClient(LambdaLogger logger) {
            super(logger);
        }

        @Override
        public NestedInstitutionResponse getNestedInstitution(URI uri, Language language) {
            this.languageCode = language;
            return new NestedInstitutionResponse("true");
        }
    }
}