package no.unit.nva.institution.proxy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.util.function.Function;
import no.unit.nva.institution.proxy.CristinApiClient;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.utils.LanguageMapper;
import nva.commons.handlers.ApiGatewayHandler;
import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;
import org.apache.http.HttpStatus;

public class InstitutionListHandler extends ApiGatewayHandler<Void, InstitutionListResponse> {

    public static final String LANGUAGE_QUERY_PARAMETER = "language";
    private final Function<LambdaLogger, CristinApiClient> cristinApiClientSupplier;

    @JacocoGenerated
    public InstitutionListHandler() {
        this(new Environment(), CristinApiClient::new);
    }

    /**
     * In testing, it is necessary to pass the environment to the constructor.
     */
    public InstitutionListHandler(Environment environment, Function<LambdaLogger, CristinApiClient> cristinApiClient) {
        super(Void.class, environment);
        this.cristinApiClientSupplier = cristinApiClient;
    }

    @Override
    protected InstitutionListResponse processInput(Void input, RequestInfo requestInfo,
                                                   Context context)
        throws UnknownLanguageException, GatewayException {
        CristinApiClient cristinApiClient = cristinApiClientSupplier.apply(logger);
        String languageParameter = requestInfo.getQueryParameters().get(LANGUAGE_QUERY_PARAMETER);
        LanguageMapper languageMapper = new LanguageMapper(logger);
        return cristinApiClient.getInstitutions(languageMapper.getLanguage(languageParameter));
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, InstitutionListResponse output) {
        return HttpStatus.SC_OK;
    }
}