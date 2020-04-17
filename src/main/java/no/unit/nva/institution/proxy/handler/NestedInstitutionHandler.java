package no.unit.nva.institution.proxy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import no.unit.nva.institution.proxy.CristinApiClient;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.UnrecognizedUriException;
import no.unit.nva.institution.proxy.request.NestedInstitutionRequest;
import no.unit.nva.institution.proxy.response.NestedInstitutionResponse;
import no.unit.nva.institution.proxy.utils.Language;
import no.unit.nva.institution.proxy.utils.LanguageMapper;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.handlers.ApiGatewayHandler;
import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;
import org.apache.http.HttpStatus;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class NestedInstitutionHandler extends ApiGatewayHandler<NestedInstitutionRequest, NestedInstitutionResponse> {

    public static final String LOG_URI_ERROR_TEMPLATE = "The supplied URI <%s> was invalid";

    private final Function<LambdaLogger, CristinApiClient> cristinApiClientSupplier;

    @JacocoGenerated
    public NestedInstitutionHandler() {
        this(new Environment(), CristinApiClient::new);
    }

    /**
     * In testing, it is necessary to pass the environment to the constructor.
     */
    public NestedInstitutionHandler(Environment environment,
                                    Function<LambdaLogger, CristinApiClient> cristinApiClientSupplier) {
        super(NestedInstitutionRequest.class, environment);
        this.cristinApiClientSupplier = cristinApiClientSupplier;
    }

    @Override
    protected NestedInstitutionResponse processInput(NestedInstitutionRequest input,
                                                     RequestInfo requestInfo,
                                                     Context context) throws ApiGatewayException {
        CristinApiClient cristinApiClient = cristinApiClientSupplier.apply(logger);
        LanguageMapper languageMapper = new LanguageMapper(logger);
        URI uri = parseUri(input.getUri());
        Language language = languageMapper.getLanguage(input.getLanguage());
        if (uri.getPath().contains("institutions")) {
            return cristinApiClient.getNestedInstitution(uri, language);
        } else if (uri.getPath().contains("units")) {
            try {
                return cristinApiClient.getSingleUnit(uri, language);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            throw new UnrecognizedUriException(uri);
        }
        return null;
    }

    @Override
    protected Integer getSuccessStatusCode(NestedInstitutionRequest input, NestedInstitutionResponse output) {
        return HttpStatus.SC_OK;
    }

    private URI parseUri(String uri) throws InvalidUriException {
        try {
            return URI.create(uri);
        } catch (Exception e) {
            logger.log(String.format(LOG_URI_ERROR_TEMPLATE, uri));
            throw new InvalidUriException(uri);
        }
    }
}
