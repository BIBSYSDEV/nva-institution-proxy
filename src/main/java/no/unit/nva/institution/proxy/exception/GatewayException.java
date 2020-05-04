package no.unit.nva.institution.proxy.exception;

import nva.commons.exceptions.ApiGatewayException;
import org.apache.http.HttpStatus;

public class GatewayException extends ApiGatewayException {

    public static final int ERROR_CODE = HttpStatus.SC_BAD_GATEWAY;

    public GatewayException(String message) {
        super(message);
    }

    public GatewayException(Exception cause) {
        super(cause);
    }

    @Override
    protected Integer statusCode() {
        return ERROR_CODE;
    }
}
