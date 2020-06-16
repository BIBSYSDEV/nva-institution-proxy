package no.unit.nva.institution.proxy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.JsonNode;
import no.unit.nva.institution.proxy.CristinApiClient;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.MissingParameterException;
import no.unit.nva.institution.proxy.exception.UnrecognizedUriException;
import no.unit.nva.institution.proxy.utils.Language;
import no.unit.nva.institution.proxy.utils.LanguageMapper;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.handlers.ApiGatewayHandler;
import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;
import nva.commons.utils.attempt.Failure;
import org.apache.http.HttpStatus;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static nva.commons.utils.attempt.Try.attempt;

public class NestedInstitutionHandler extends ApiGatewayHandler<Void, JsonNode> {

    public static final String LOG_URI_ERROR_TEMPLATE = "The supplied URI <%s> was invalid";
    public static final String URI_QUERY_PARAMETER = "uri";
    public static final String LANGUAGE_QUERY_PARAMETER = "language";
    public static final String PARAMETER_NOT_FOUND_ERROR_MESSAGE = "Parameter not found:";

    private final CristinApiClient cristinApiClient;
    private final Map<String, JsonNode> cache = new ConcurrentHashMap<>();

    @JacocoGenerated
    public NestedInstitutionHandler() {
        this(new Environment(), new CristinApiClient());
    }

    /**
     * In testing, it is necessary to pass the environment to the constructor.
     */
    public NestedInstitutionHandler(Environment environment, CristinApiClient cristinApiClient) {
        super(Void.class, environment, LoggerFactory.getLogger(NestedInstitutionHandler.class));
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

        String key = uri.toString() + language.getCode();
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        JsonNode response;
        if (uri.getPath().contains("institutions")) {
            response = cristinApiClient.getNestedInstitution(uri, language);
            cache.put(key, response);
            return response;
        } else if (uri.getPath().contains("units")) {
            try {
                response = cristinApiClient.getSingleUnit(uri, language);
                cache.put(key, response);
                return response;
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
