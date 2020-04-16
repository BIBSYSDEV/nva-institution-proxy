package no.unit.nva.institution.proxy.handler;

import static nva.commons.utils.JsonUtils.jsonParser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Function;
import no.unit.nva.institution.proxy.CristinApiClient;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.request.InstitutionListRequest;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import nva.commons.handlers.GatewayResponse;
import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.IoUtils;
import nva.commons.utils.TestLogger;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

public class InstitutionListHandlerTest {

    private static final String SOME_ENV_VALUE = "ANY_VALUE";

    public static final String EVENTS_FOLDER = "events";

    public static final Path INSTITUTIONS_REQUEST_WITH_NON_EMPTY_BODY =
        Path.of(EVENTS_FOLDER, "institutions_request_with_non_empty_body.json");
    public static final String LANGUAGE_STRING_VALUE_IN_RESOURCE_FILE = "nonEmptyLanguageString";

    private static final String SOME_EXCEPTION_MESSAGE = "This is the exception message";

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

    @DisplayName("Check that the InstitutionListHandler exists")
    @Test
    void mainHandlerExists() {
        new InstitutionListHandler(environment, CristinApiClient::new);
    }

    @DisplayName("processInput receives the correct input")
    @Test
    public void processInputReceivesTheCorrectInput()
        throws IOException {
        InstitutionListHandlerWithGetRequest handler = institutionHandlerWithAccessibleRequestObject();

        InputStream inputStream = inputNonEmptyLangugeCode();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(inputStream, outputStream, context);
        MatcherAssert.assertThat(handler.getRequest().getLanguage(),
            is(equalTo(LANGUAGE_STRING_VALUE_IN_RESOURCE_FILE)));
    }

    @DisplayName("handleRequest returns OK to the client when the processing is successful")
    @Test
    public void handleRequestReturnsOkToTheClientWhenTheProcessingIsSuccessful() throws IOException {
        InstitutionListHandlerWithGetRequest handler = institutionHandlerWithAccessibleRequestObject();

        InputStream inputStream = inputNonEmptyLangugeCode();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        String outputString = outputStream.toString(StandardCharsets.UTF_8);
        GatewayResponse<InstitutionListResponse> response = jsonParser.readValue(outputString, GatewayResponse.class);
        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @DisplayName("handleRequest returns BadRequest to the client when UnknownLanguageException occurs")
    @Test
    public void handleRequestReturnsBadRequestWhenUnknownLanguageExceptionOccurs()
        throws IOException, UnknownLanguageException, GatewayException {
        InstitutionListHandler handler = handlerThatThrowsUnknownLanguageException(SOME_EXCEPTION_MESSAGE);
        InputStream inputStream = inputNonEmptyLangugeCode();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBodyObject(Problem.class).getDetail(), containsString(SOME_EXCEPTION_MESSAGE));
    }

    @DisplayName("handleRequest returns BadGateway to the client when GatewayException occurs")
    @Test
    public void handleRequestReturnsBadRequestWhenInstitutionFailureOccurs()
        throws IOException, UnknownLanguageException, GatewayException {
        InstitutionListHandler handler = handlerThatThrowsInstitutionFailureException(SOME_EXCEPTION_MESSAGE);
        InputStream inputStream = inputNonEmptyLangugeCode();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_GATEWAY));
        assertThat(response.getBodyObject(Problem.class).getDetail(), containsString(SOME_EXCEPTION_MESSAGE));
    }

    private GatewayResponse<Problem> gatewayResponseWithProblem(ByteArrayOutputStream outputStream)
        throws IOException {
        String outputString = outputStream.toString(StandardCharsets.UTF_8);
        TypeReference<GatewayResponse<Problem>> ref = new TypeReference<>() {};
        return jsonParser.readValue(outputString, ref);
    }

    private InstitutionListHandler handlerThatThrowsUnknownLanguageException(String exceptionMessage)
        throws UnknownLanguageException, GatewayException {
        CristinApiClient cristinClient = mock(CristinApiClient.class);
        when(cristinClient.getInstitutions(anyString())).thenThrow(new UnknownLanguageException(exceptionMessage));
        return new InstitutionListHandler(environment, logger -> cristinClient);
    }

    private InstitutionListHandler handlerThatThrowsInstitutionFailureException(String exceptionMessage)
        throws UnknownLanguageException, GatewayException {
        CristinApiClient cristinClient = mock(CristinApiClient.class);
        IOException cause = new IOException(exceptionMessage);
        when(cristinClient.getInstitutions(anyString())).thenThrow(new GatewayException(cause));
        return new InstitutionListHandler(environment, logger -> cristinClient);
    }

    private InstitutionListHandlerWithGetRequest institutionHandlerWithAccessibleRequestObject() {
        MockCristinApiClient cristinApiClient = new MockCristinApiClient(logger);
        return new InstitutionListHandlerWithGetRequest(environment, logger -> cristinApiClient);
    }

    private InputStream inputNonEmptyLangugeCode() {
        String input = IoUtils.stringFromResources(INSTITUTIONS_REQUEST_WITH_NON_EMPTY_BODY);
        return IoUtils.stringToStream(input);
    }

    private static class MockCristinApiClient extends CristinApiClient {

        private String languageCode;

        protected MockCristinApiClient(LambdaLogger logger) {
            super(logger);
        }

        @Override
        public InstitutionListResponse getInstitutions(String languageCode) {
            this.languageCode = languageCode;
            return new InstitutionListResponse(Collections.emptyList());
        }
    }

    private static class InstitutionListHandlerWithGetRequest extends InstitutionListHandler {

        private InstitutionListRequest request;

        public InstitutionListHandlerWithGetRequest(Environment environment,
                                                    Function<LambdaLogger, CristinApiClient> cristinApiClient) {
            super(environment, cristinApiClient);
        }

        @Override
        protected InstitutionListResponse processInput(InstitutionListRequest input, RequestInfo requestInfo,
                                                       Context context)
            throws UnknownLanguageException, GatewayException {
            this.request = input;
            return super.processInput(input, requestInfo, context);
        }

        public InstitutionListRequest getRequest() {
            return request;
        }
    }
}
