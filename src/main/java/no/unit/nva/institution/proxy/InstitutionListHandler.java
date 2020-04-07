package no.unit.nva.institution.proxy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.util.function.Function;
import no.unit.nva.institution.proxy.exception.InstitutionFailureException;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import nva.commons.hanlders.ApiGatewayHandler;
import nva.commons.hanlders.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;
import org.apache.http.HttpStatus;

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
        throws UnknownLanguageException, InstitutionFailureException {
        CristinApiClient cristinApiClient = cristinApiClientSupplier.apply(context.getLogger());
        return cristinApiClient.getInstitutions(input.getLanguage());
    }

    @Override
    protected Integer getSuccessStatusCode(InstitutionListRequest input, InstitutionListResponse output) {
        return HttpStatus.SC_OK;
    }
}