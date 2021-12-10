package no.unit.nva.institution.proxy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.institution.proxy.CristinApiClient;
import no.unit.nva.institution.proxy.exception.HttpClientFailureException;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.utils.Language;
import nva.commons.apigateway.GatewayResponse;
import nva.commons.apigateway.RequestInfo;
import nva.commons.core.Environment;
import nva.commons.core.JsonUtils;
import nva.commons.core.ioutils.IoUtils;
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
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InstitutionListHandlerTest extends HandlerTest {

    private static final String SOME_ENV_VALUE = "ANY_VALUE";

    public static final String EVENTS_FOLDER = "events";

    public static final Path INSTITUTIONS_REQUEST_WITH_VALID_LANGUAGE_PARAMETER =
        Path.of(EVENTS_FOLDER, "institutions_request_with_valid_language_parameter.json");
    public static final Path INSTITUTIONS_REQUEST_WITH_INVALID_LANGUAGE =
        Path.of(EVENTS_FOLDER, "institutions_request_with_invalid_language.json");
    public static final Path INSTITUTIONS_REQUEST_WITH_NO_QUERY_PARAMETERS =
        Path.of(EVENTS_FOLDER, "institutions_request_with_no_query_parameters.json");

    public static final String LANGUAGE_STRING_VALUE_IN_RESOURCE_FILE = "en";

    private static final String SOME_EXCEPTION_MESSAGE = "This is the exception message";
    public static final ObjectMapper objectMapper = JsonUtils.dtoObjectMapper;
    private Environment environment;
    private Context context;


    /**
     * Setup.
     */
    @BeforeEach
    public void setup() {
        environment = mock(Environment.class);

        when(environment.readEnv(anyString())).thenReturn(SOME_ENV_VALUE);
        context = mock(Context.class);
    }

    @DisplayName("Check that the InstitutionListHandler exists")
    @Test
    void mainHandlerExists() {
        new InstitutionListHandler(environment, new CristinApiClient());
    }

    @DisplayName("processInput receives the correct input")
    @Test
    public void processInputReceivesTheCorrectInput()
        throws IOException {
        InstitutionListHandlerWithGetRequest handler = institutionHandlerWithAccessibleRequestObject();

        InputStream inputStream = inputValidLanguageCode();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(inputStream, outputStream, context);
        assertThat(handler.getLanguageQueryParameter(),
            is(equalTo(LANGUAGE_STRING_VALUE_IN_RESOURCE_FILE)));
    }

    @DisplayName("handleRequest returns OK to the client when the processing is successful")
    @Test
    public void handleRequestReturnsOkToTheClientWhenTheProcessingIsSuccessful() throws IOException {
        InstitutionListHandlerWithGetRequest handler = institutionHandlerWithAccessibleRequestObject();

        InputStream inputStream = inputValidLanguageCode();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<InstitutionListResponse> response = gatewayResponseWithSuccessfulResult(outputStream);
        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @DisplayName("handleRequest returns BadRequest to the client when UnknownLanguageException occurs")
    @Test
    public void handleRequestReturnsBadRequestWhenUnknownLanguageExceptionOccurs()
        throws IOException {
        InstitutionListHandler handler = handlerWithNonOperativeCristinClient();
        InputStream inputStream = inputInvalidLanguage();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);
        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBodyObject(Problem.class).getDetail(),
            containsString(INVALID_LANGUAGE_IN_RESOURCES_FILE));
    }

    @DisplayName("handleRequest returns BadGateway to the client when GatewayException occurs")
    @Test
    public void handleRequestReturnsBadGatewayToTheClientWhnGatewayExceptionOccurs()
        throws IOException, HttpClientFailureException {
        InstitutionListHandler handler = handlerThatThrowsInstitutionFailureException(SOME_EXCEPTION_MESSAGE);
        InputStream inputStream = inputValidLanguageCode();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        handler.handleRequest(inputStream, outputStream, context);

        GatewayResponse<Problem> response = gatewayResponseWithProblem(outputStream);

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_GATEWAY));
        assertThat(response.getBodyObject(Problem.class).getDetail(), containsString(SOME_EXCEPTION_MESSAGE));
    }

    @DisplayName("processInput throws no exception when query-parameters-map is missing")
    @Test
    public void processInputThrowsNoExceptionWhenQueryParametersMapIsMissing() throws IOException {
        InstitutionListHandler handler = handlerWithNonOperativeCristinClient();
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setQueryParameters(null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(inputNoQueryParameters(), outputStream, context);

        GatewayResponse<InstitutionListResponse> response = gatewayResponseWithSuccessfulResult(outputStream);
        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    private GatewayResponse<InstitutionListResponse> gatewayResponseWithSuccessfulResult(
        ByteArrayOutputStream outputStream) throws JsonProcessingException {
        String outputString = outputStream.toString(StandardCharsets.UTF_8);
        return objectMapper.readValue(outputString, GatewayResponse.class);
    }

    private GatewayResponse<Problem> gatewayResponseWithProblem(ByteArrayOutputStream outputStream)
        throws IOException {
        String outputString = outputStream.toString(StandardCharsets.UTF_8);
        TypeReference<GatewayResponse<Problem>> ref = new TypeReference<>() {};
        return objectMapper.readValue(outputString, ref);
    }

    private InstitutionListHandler handlerWithNonOperativeCristinClient() {

        CristinApiClient cristinClient = mock(CristinApiClient.class);
        return new InstitutionListHandler(environment, cristinClient);
    }

    private InstitutionListHandler handlerThatThrowsInstitutionFailureException(String exceptionMessage)
        throws HttpClientFailureException {
        CristinApiClient cristinClient = mock(CristinApiClient.class);
        IOException cause = new IOException(exceptionMessage);
        when(cristinClient.getInstitutions(any(Language.class))).thenThrow(new HttpClientFailureException(cause));
        return new InstitutionListHandler(environment, cristinClient);
    }

    private InstitutionListHandlerWithGetRequest institutionHandlerWithAccessibleRequestObject() {
        MockCristinApiClient cristinApiClient = new MockCristinApiClient();
        return new InstitutionListHandlerWithGetRequest(environment, cristinApiClient);
    }

    private InputStream inputInvalidLanguage() {
        return IoUtils.inputStreamFromResources(INSTITUTIONS_REQUEST_WITH_INVALID_LANGUAGE);
    }

    private InputStream inputValidLanguageCode() {
        return IoUtils.inputStreamFromResources(INSTITUTIONS_REQUEST_WITH_VALID_LANGUAGE_PARAMETER);
    }

    private InputStream inputNoQueryParameters() {
        return IoUtils.inputStreamFromResources(INSTITUTIONS_REQUEST_WITH_NO_QUERY_PARAMETERS);
    }

    private static class MockCristinApiClient extends CristinApiClient {

        protected MockCristinApiClient() {
            super();
        }

        @Override
        public InstitutionListResponse getInstitutions(Language language) {
            return new InstitutionListResponse(Collections.emptyList());
        }
    }

    private static class InstitutionListHandlerWithGetRequest extends InstitutionListHandler {

        private String languageQueryParameter;

        public InstitutionListHandlerWithGetRequest(Environment environment, CristinApiClient cristinApiClient) {
            super(environment, cristinApiClient);
        }

        @Override
        protected InstitutionListResponse processInput(Void input, RequestInfo requestInfo,
                                                       Context context)
            throws UnknownLanguageException, HttpClientFailureException {
            this.languageQueryParameter = requestInfo.getQueryParameters()
                .get(InstitutionListHandler.LANGUAGE_QUERY_PARAMETER);
            return super.processInput(input, requestInfo, context);
        }

        public String getLanguageQueryParameter() {
            return languageQueryParameter;
        }
    }
}
