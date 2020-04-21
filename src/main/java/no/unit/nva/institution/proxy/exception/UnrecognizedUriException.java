package no.unit.nva.institution.proxy.exception;

import java.net.URI;
import nva.commons.exceptions.ApiGatewayException;
import org.apache.http.HttpStatus;

public class UnrecognizedUriException extends ApiGatewayException {

    public static final String ERROR_MESSAGE = "The requested URI <%s> was unrecognized";

    public UnrecognizedUriException(URI uri) {
        super(String.format(ERROR_MESSAGE, uri.toString()));
    }

    @Override
    protected Integer statusCode() {
        return HttpStatus.SC_BAD_REQUEST;
    }
}
