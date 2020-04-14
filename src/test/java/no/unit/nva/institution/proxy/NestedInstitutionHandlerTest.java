package no.unit.nva.institution.proxy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.hanlders.GatewayResponse;
import nva.commons.hanlders.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.IoUtils;
import nva.commons.utils.TestLogger;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Function;

import static nva.commons.utils.JsonUtils.jsonParser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NestedInstitutionHandlerTest {

    private static final String SOME_ENV_VALUE = "SOME_VALUE";
    public static final String EVENTS_FOLDER = "events";
    private static final Path NESTED_INSTITUTIONS_REQUEST_WITH_NON_EMPTY_BODY =
            Path.of(EVENTS_FOLDER, "nested_institutions_request_with_non_empty_body.json");
    public static final String LANGUAGE_STRING_VALUE_IN_RESOURCE_FILE = "nonEmptyLanguageString";
    public static final String URI_STRING_VALUE_IN_RESOURCE_FILE = "nonEmptyUriString";
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

    @Test
    void itWorks() throws ApiGatewayException {
        NestedInstitutionHandler nestedInstitutionHandler =
                new NestedInstitutionHandler(environment, (a) -> new CristinApiClient(logger));
        NestedInstitutionRequest request =
                new NestedInstitutionRequest("https://api.cristin.no/v2/institutions/194", "en");
        RequestInfo requestInfo = new RequestInfo();
        Context context = mock(Context.class);

        NestedInstitutionResponse repsonse = nestedInstitutionHandler.processInput(request, requestInfo, context);
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
        InputStream inputStream = inputNonEmptyRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(inputStream, outputStream, context);
        assertThat(handler.getRequest().getLanguage(), is(equalTo(LANGUAGE_STRING_VALUE_IN_RESOURCE_FILE)));
        assertThat(handler.getRequest().getUri(), is(equalTo(URI_STRING_VALUE_IN_RESOURCE_FILE)));
    }


    @DisplayName("handleRequest returns OK to the client when the processing is successful")
    @Test
    public void handleRequestReturnsOkToTheClientWhenTheProcessingIsSuccessful() throws IOException {
        NestedInstitutionHandlerWithGetRequest handler = nestedInstitutionHandlerWithAccessibleRequestObject();

        InputStream inputStream = inputNonEmptyRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        String outputString = outputStream.toString(StandardCharsets.UTF_8);
        GatewayResponse<InstitutionListResponse> response = jsonParser.readValue(outputString, GatewayResponse.class);
        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @DisplayName("handleRequest returns BadRequest to the client when UnknownLanguageException occurs")
    @Test
    public void handleRequestReturnsBadRequestWhenUnknownLanguageExceptionOccurs()
            throws IOException, UnknownLanguageException, GatewayException, InvalidUriException {
        NestedInstitutionHandler handler = handlerThatThrowsUnknownLanguageException(SOME_EXCEPTION_MESSAGE);
        InputStream inputStream = inputNonEmptyRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBody().getDetail(), containsString(SOME_EXCEPTION_MESSAGE));
    }

    @DisplayName("handleRequest returns BadRequest to the client when InvalidUriException occurs")
    @Test
    public void handleRequestReturnsBadRequestWhenInvalidUriExceptionOccurs()
            throws IOException, UnknownLanguageException, GatewayException, InvalidUriException {
        NestedInstitutionHandler handler = handlerThatThrowsInvalidUriException(SOME_EXCEPTION_MESSAGE);
        InputStream inputStream = inputNonEmptyRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBody().getDetail(), containsString(SOME_EXCEPTION_MESSAGE));
    }

    @DisplayName("handleRequest returns BadGateway to the client when GatewayException occurs")
    @Test
    public void handleRequestReturnsBadRequestWhenInstitutionFailureOccurs()
            throws IOException, InvalidUriException, GatewayException, UnknownLanguageException {
        NestedInstitutionHandler handler = handlerThatThrowsNestedInstitutionFailureException(SOME_EXCEPTION_MESSAGE);
        InputStream inputStream = inputNonEmptyRequest();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_GATEWAY));
        assertThat(response.getBody().getDetail(), containsString(SOME_EXCEPTION_MESSAGE));
    }

    private NestedInstitutionHandler handlerThatThrowsNestedInstitutionFailureException(String message) throws
            InvalidUriException, GatewayException, UnknownLanguageException {
        CristinApiClient cristinClient = mock(CristinApiClient.class);
        IOException cause = new IOException(message);
        when(cristinClient.getNestedInstitution(anyString(), anyString())).thenThrow(new GatewayException(cause));
        return new NestedInstitutionHandler(environment, logger -> cristinClient);
    }

    private GatewayResponse<Problem> gatewayResponseWithProblem(ByteArrayOutputStream outputStream)
            throws IOException {
        String outputString = outputStream.toString(StandardCharsets.UTF_8);
        TypeReference<GatewayResponse<Problem>> ref = new TypeReference<>() {
        };
        return jsonParser.readValue(outputString, ref);
    }

    private NestedInstitutionHandler handlerThatThrowsUnknownLanguageException(String exceptionMessage) throws
            InvalidUriException, GatewayException, UnknownLanguageException {
        CristinApiClient cristinClient = mock(CristinApiClient.class);
        when(cristinClient.getNestedInstitution(anyString(), anyString()))
                .thenThrow(new UnknownLanguageException(exceptionMessage));
        return new NestedInstitutionHandler(environment, logger -> cristinClient);
    }

    private NestedInstitutionHandler handlerThatThrowsInvalidUriException(String exceptionMessage) throws
            InvalidUriException, GatewayException, UnknownLanguageException {
        CristinApiClient cristinClient = mock(CristinApiClient.class);
        when(cristinClient.getNestedInstitution(anyString(), anyString()))
                .thenThrow(new InvalidUriException(exceptionMessage));
        return new NestedInstitutionHandler(environment, logger -> cristinClient);
    }

    private InputStream inputNonEmptyRequest() {
        String input = IoUtils.stringFromResources(NESTED_INSTITUTIONS_REQUEST_WITH_NON_EMPTY_BODY);
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
        private String languageCode;
        private String uri;

        protected MockCristinApiClient(LambdaLogger logger) {
            super(logger);
        }

        @Override
        public NestedInstitutionResponse getNestedInstitution(String uri, String languageCode) {
            this.languageCode = languageCode;
            return new NestedInstitutionResponse("true");
        }
    }
}