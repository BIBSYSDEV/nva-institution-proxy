package no.unit.nva.institution.proxy.exception;

import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.apache.http.HttpStatus;

public class JsonParsingException extends ApiGatewayException {

    public static final String ERROR_MESSAGE_PATTERN = "Could not parse string into valid json object:";

    public JsonParsingException(Exception exception, String jsonString) {
        super(exception, ERROR_MESSAGE_PATTERN + jsonString);
    }

    @Override
    protected Integer statusCode() {
        return HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }
}
