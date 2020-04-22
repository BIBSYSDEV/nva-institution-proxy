package no.unit.nva.institution.proxy.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

public class MissingParameterExceptionTest {

    @Test
    public void missingParametersExceptionReturnsStatusBadRequest() {
        String message = "someMessage";
        MissingParameterException exception = new MissingParameterException(message);
        assertThat(exception.statusCode(), is(equalTo(HttpStatus.SC_BAD_REQUEST)));
    }
}