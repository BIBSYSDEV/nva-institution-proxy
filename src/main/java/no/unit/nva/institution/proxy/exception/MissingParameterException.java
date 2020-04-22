package no.unit.nva.institution.proxy.exception;

import nva.commons.exceptions.ApiGatewayException;
import org.apache.http.HttpStatus;

public class MissingParameterException extends ApiGatewayException {

    public static final String MESSAGE_PATTERN = "Parameter not be found:";

    public MissingParameterException(String parameterName) {
        super(String.format(MESSAGE_PATTERN + parameterName));
    }

    @Override
    protected Integer statusCode() {
        return HttpStatus.SC_BAD_REQUEST;
    }
}
