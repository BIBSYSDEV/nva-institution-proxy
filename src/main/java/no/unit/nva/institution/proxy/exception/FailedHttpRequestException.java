package no.unit.nva.institution.proxy.exception;

import nva.commons.exceptions.ApiGatewayException;
import org.apache.http.HttpStatus;

public class FailedHttpRequestException extends ApiGatewayException {

    public static Integer ERROR_CODE = HttpStatus.SC_BAD_GATEWAY;

    public FailedHttpRequestException(String message) {
        super(message);
    }

    public FailedHttpRequestException(Exception exception) {
        super(exception);
    }

    public FailedHttpRequestException(Exception exception, String message) {
        super(exception, message);
    }

    @Override
    protected Integer statusCode() {
        return ERROR_CODE;
    }
}
