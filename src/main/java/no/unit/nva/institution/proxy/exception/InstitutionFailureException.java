package no.unit.nva.institution.proxy.exception;

import nva.commons.exceptions.ApiGatewayException;
import org.apache.http.HttpStatus;

public class InstitutionFailureException extends ApiGatewayException {

    public static final int ERROR_CODE = HttpStatus.SC_BAD_GATEWAY;

    public InstitutionFailureException(Exception cause) {
        super(cause);
    }

    @Override
    protected Integer statusCode() {
        return ERROR_CODE;
    }
}
