package no.unit.nva.institution.proxy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.institution.proxy.CristinApiClient;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.MissingParameterException;
import no.unit.nva.institution.proxy.exception.UnrecognizedUriException;
import no.unit.nva.institution.proxy.utils.Language;
import no.unit.nva.institution.proxy.utils.LanguageMapper;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.JsonUtils;
import nva.commons.core.attempt.Failure;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Optional;

import static java.util.Objects.isNull;
import static nva.commons.core.attempt.Try.attempt;

public class NestedInstitutionHandler extends ApiGatewayHandler<Void, JsonNode> {

    public static final String LOG_URI_ERROR_TEMPLATE = "The supplied URI <%s> was invalid";
    public static final String URI_QUERY_PARAMETER = "uri";
    public static final String LANGUAGE_QUERY_PARAMETER = "language";
    public static final String PARAMETER_NOT_FOUND_ERROR_MESSAGE = "Parameter not found:";

    private final CristinApiClient cristinApiClient;
    private static final ObjectMapper objectMapper = JsonUtils.dtoObjectMapper;
    public static final Logger logger =  LoggerFactory.getLogger(NestedInstitutionHandler.class);

    @JacocoGenerated
    public NestedInstitutionHandler() {
        this(new Environment(), new CristinApiClient());
    }

    /**
     * In testing, it is necessary to pass the environment to the constructor.
     */
    public NestedInstitutionHandler(Environment environment, CristinApiClient cristinApiClient) {
        super(Void.class, environment, objectMapper);
        this.cristinApiClient = cristinApiClient;
    }

    @Override
    protected JsonNode processInput(Void input,
                                    RequestInfo requestInfo,
                                    Context context) throws ApiGatewayException {

        LanguageMapper languageMapper = new LanguageMapper();
        URI uri = getDepartmentUriFromQueryParameters(requestInfo);
        String languageCode = getLanguageCodFromQueryParameters(requestInfo);
        Language language = languageMapper.getLanguage(languageCode);

        if (uri.getPath().contains("institutions")) {
            return cristinApiClient.getNestedInstitution(uri, language);
        } else if (uri.getPath().contains("units")) {
            try {
                return cristinApiClient.getSingleUnit(uri, language);
            } catch (InterruptedException e) {
                logger.error("Error probably in HttpClient", e);
            }
        } else {
            throw new UnrecognizedUriException(uri);
        }

        return null;
    }

    private URI getDepartmentUriFromQueryParameters(RequestInfo requestInfo) throws ApiGatewayException {
        return attempt(() -> requestInfo.getQueryParameters().get(URI_QUERY_PARAMETER))
            .map(URI::create)
            .orElseThrow(failure -> handleUriParsingFailure(failure, requestInfo));
    }

    private String getLanguageCodFromQueryParameters(RequestInfo requestInfo) {
        return requestInfo.getQueryParameters().get(LANGUAGE_QUERY_PARAMETER);
    }

    private ApiGatewayException handleUriParsingFailure(Failure<URI> failure, RequestInfo requestInfo) {
        return handleMissingUri(requestInfo).orElse(handleMalformedUri(failure, requestInfo));
    }

    private ApiGatewayException handleMalformedUri(Failure<URI> failure, RequestInfo requestInfo) {
        String malformedUri = requestInfo.getQueryParameters().get(URI_QUERY_PARAMETER);
        logger.warn(String.format(LOG_URI_ERROR_TEMPLATE, malformedUri));
        return new InvalidUriException(failure.getException(), malformedUri);
    }

    private Optional<ApiGatewayException> handleMissingUri(RequestInfo requestInfo) {
        if (isNull(requestInfo.getQueryParameters().get(URI_QUERY_PARAMETER))) {
            logger.warn(PARAMETER_NOT_FOUND_ERROR_MESSAGE + URI_QUERY_PARAMETER);
            return Optional.of(new MissingParameterException(URI_QUERY_PARAMETER));
        }
        return Optional.empty();
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, JsonNode output) {
        return HttpStatus.SC_OK;
    }
}
