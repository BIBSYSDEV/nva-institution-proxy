package no.unit.nva.institution.proxy.exception;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InvalidUriExceptionTest {

    @DisplayName("InvalidUriException returns BAD_GATEWAY as error code")
    @Test
    public void invalidUriExceptionReturnsBadGatewayAsErrorCode() {
        InvalidUriException exception = new InvalidUriException("Some message");
        assertThat(exception.getStatusCode(), is(equalTo(HttpStatus.SC_BAD_REQUEST)));
    }
}