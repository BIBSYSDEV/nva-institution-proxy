package no.unit.nva.institution.proxy;

import com.amazonaws.services.lambda.runtime.Context;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.hanlders.ApiGatewayHandler;
import nva.commons.hanlders.RequestInfo;
import nva.commons.utils.Environment;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstitutionListHandler extends ApiGatewayHandler<InstitutionListRequest, InstitutionListResponse> {


    private final Map<Object, Object> exceptionToStatus;

    /* default */ InstitutionListHandler() {
        super(InstitutionListRequest.class);
        exceptionToStatus = new HashMap<>();
        exceptionToStatus.put(IllegalStateException.class.getName(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * In testing, it is necessary to pass the environment to the constructor.
     *
     * @param environment the environment to be used in testing
     */
    public InstitutionListHandler(Environment environment) {
        super(InstitutionListRequest.class, environment);
        exceptionToStatus = new HashMap<>();
        exceptionToStatus.put(IllegalStateException.class.getName(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Override
    protected InstitutionListResponse processInput(
            InstitutionListRequest input, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        CristinApiClient cristinApiClient = new CristinApiClient(context.getLogger());
        InstitutionListResponse response = null;
        try {
            List<InstitutionResponse> institutionResponses = cristinApiClient.getInstitutions(input.getLanguage());
            response = new InstitutionListResponse(institutionResponses);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected Integer getSuccessStatusCode(InstitutionListRequest input, InstitutionListResponse output) {
        return HttpStatus.SC_OK;
    }

}
