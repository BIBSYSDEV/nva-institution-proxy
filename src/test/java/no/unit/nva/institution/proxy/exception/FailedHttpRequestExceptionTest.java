package no.unit.nva.institution.proxy.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FailedHttpRequestExceptionTest {

    public static final String SOME_MESSAGE = "something";

    @Test
    @DisplayName("statusCode returns the defined status code")
    void statusCodeReturnsTheDefinedStatusCode() {
        FailedHttpRequestException exception = new FailedHttpRequestException("some message");
        assertThat(exception.statusCode(), is(equalTo(FailedHttpRequestException.ERROR_CODE)));
    }

    @Test
    @DisplayName("FailedRequestException can be created by using a message string")
    public void failedRequestExceptionCanBeCreatedByUsingAMessageString() {
        FailedHttpRequestException exception = new FailedHttpRequestException(SOME_MESSAGE);
        assertNotNull(exception);
    }

    @Test
    @DisplayName("FailedRequestException can be created using another exception as cause")
    public void failedRequestExceptionCanBeCreatedByUsingAnotherExceptionAsCause() {
        IOException cause = new IOException(SOME_MESSAGE);
        FailedHttpRequestException exception = new FailedHttpRequestException(cause);
        assertNotNull(exception);
    }

    @Test
    @DisplayName("FailedRequestException can be created by using a message string and a cause")
    public void failedRequestExceptionCanBeCreatedByUsingAMessageStringAndACause() {
        IOException cause = new IOException(SOME_MESSAGE);
        FailedHttpRequestException exception = new FailedHttpRequestException(cause, SOME_MESSAGE);
        assertNotNull(exception);
    }
}