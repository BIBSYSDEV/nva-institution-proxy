package no.unit.nva.institution.proxy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import no.unit.nva.institution.proxy.CristinApiClient;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.request.InstitutionListRequest;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.utils.LanguageMapper;
import nva.commons.handlers.ApiGatewayHandler;
import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;
import org.apache.http.HttpStatus;

import java.util.function.Function;

public class InstitutionListHandler extends ApiGatewayHandler<InstitutionListRequest, InstitutionListResponse> {

    private final Function<LambdaLogger, CristinApiClient> cristinApiClientSupplier;

    @JacocoGenerated
    public InstitutionListHandler() {
        this(new Environment(), CristinApiClient::new);
    }

    /**
     * In testing, it is necessary to pass the environment to the constructor.
     */
    public InstitutionListHandler(Environment environment, Function<LambdaLogger, CristinApiClient> cristinApiClient) {
        super(InstitutionListRequest.class, environment);
        this.cristinApiClientSupplier = cristinApiClient;
    }

    @Override
    protected InstitutionListResponse processInput(InstitutionListRequest input, RequestInfo requestInfo,
                                                   Context context)
        throws UnknownLanguageException, GatewayException {
        CristinApiClient cristinApiClient = cristinApiClientSupplier.apply(logger);
        LanguageMapper languageMapper = new LanguageMapper(logger);
        return cristinApiClient.getInstitutions(languageMapper.getLanguage(input.getLanguage()));
    }

    @Override
    protected Integer getSuccessStatusCode(InstitutionListRequest input, InstitutionListResponse output) {
        return HttpStatus.SC_OK;
    }
}