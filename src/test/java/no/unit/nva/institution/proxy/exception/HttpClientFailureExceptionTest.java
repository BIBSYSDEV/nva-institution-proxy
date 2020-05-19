package no.unit.nva.institution.proxy.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

public class HttpClientFailureExceptionTest {

    @Test
    public void gatewayExceptionHasConstructorWithStringOnly() {
        String message = "someMessage";
        HttpClientFailureException exception = new HttpClientFailureException(message);
        assertThat(exception.getMessage(), is(equalTo(message)));
        assertThat(exception.getStatusCode(), is(equalTo(HttpStatus.SC_BAD_GATEWAY)));
    }
}