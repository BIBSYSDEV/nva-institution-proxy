package no.unit.nva.institution.proxy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.util.function.Function;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.handlers.ApiGatewayHandler;
import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;
import org.apache.http.HttpStatus;

public class NestedInstitutionHandler extends ApiGatewayHandler<NestedInstitutionRequest, NestedInstitutionResponse> {

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
        return cristinApiClient.getNestedInstitution(input.getUri(), input.getLanguage());
    }

    @Override
    protected Integer getSuccessStatusCode(NestedInstitutionRequest input, NestedInstitutionResponse output) {
        return HttpStatus.SC_OK;
    }
}
