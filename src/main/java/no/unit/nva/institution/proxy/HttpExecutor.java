package no.unit.nva.institution.proxy;

import java.net.http.HttpResponse;
import no.unit.nva.institution.proxy.exception.FailedHttpRequestException;
import no.unit.nva.institution.proxy.utils.Language;
import nva.commons.exceptions.ApiGatewayException;
import org.apache.http.HttpStatus;

public interface HttpExecutor {

    int FIRST_NON_SUCCESSFUL_CODE = HttpStatus.SC_MULTIPLE_CHOICES;
    int FIRST_SUCCESSFUL_CODE = HttpStatus.SC_OK;

    InstitutionListResponse getInstitutions(Language language) throws ApiGatewayException;

    default HttpResponse<String> throwExceptionIfNotSuccessful(HttpResponse<String> response)
        throws FailedHttpRequestException {
        if (response != null &&
            response.statusCode() >= FIRST_SUCCESSFUL_CODE &&
            response.statusCode() < FIRST_NON_SUCCESSFUL_CODE
        ) {
            return response;
        }
        throw new FailedHttpRequestException(response.body());
    }
}
