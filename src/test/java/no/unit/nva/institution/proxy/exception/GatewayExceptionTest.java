package no.unit.nva.institution.proxy.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

public class GatewayExceptionTest {

    @Test
    public void gatewayExceptionHasConstructorWithStringOnly() {
        String message = "someMessage";
        GatewayException exception = new GatewayException(message);
        assertThat(exception.getMessage(), is(equalTo(message)));
        assertThat(exception.getStatusCode(), is(equalTo(HttpStatus.SC_BAD_GATEWAY)));
    }
}