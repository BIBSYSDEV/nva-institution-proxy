package no.unit.nva.institution.proxy;

import no.unit.nva.institution.proxy.exception.FailedHttpRequestException;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.utils.Language;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpExecutorTest {

    public static final String ERROR_BODY = "This is the error body";
    public static final Integer ERROR_CODE = HttpStatus.SC_BAD_REQUEST;
    HttpExecutor executor = new HttpExecutor() {
        @Override
        public InstitutionListResponse getInstitutions(Language language) {
            return null;
        }

        @Override
        public NestedInstitutionResponse getNestedInstitution(URI uri, Language language) throws GatewayException {
            return null;
        }
    };

    private static HttpResponse<String> failingResponse;
    private static HttpResponse<String> informationResponse;

    /**
     * Setup.
     */
    @BeforeAll
    public static void setup() {
        failingResponse = mock(HttpResponse.class);
        when(failingResponse.statusCode()).thenReturn(ERROR_CODE);
        when(failingResponse.body()).thenReturn(ERROR_BODY);
        informationResponse = mock(HttpResponse.class);
        when(informationResponse.statusCode()).thenReturn(HttpStatus.SC_CONTINUE);
        when(informationResponse.body()).thenReturn(ERROR_BODY);
    }

    @Test
    @DisplayName("throwExceptionIfNotSuccessful throws an exception if input is null")
    public void throwExceptionIfNotSuccessfulThrowsExceptionIfResponseIsNull() {
        assertThrows(FailedHttpRequestException.class,
            () -> executor.throwExceptionIfNotSuccessful(null));
    }

    @Test
    @DisplayName("throwExceptionIfNotSuccessful returns a message in the Exception message"
        + " saying that response was not found when input is null")
    public void throwExceptionIfNotSuccessfulReturnsAMessageSayingResponseWasNotFoundWhenInputIsNull() {
        FailedHttpRequestException exception = assertThrows(FailedHttpRequestException.class,
            () -> executor.throwExceptionIfNotSuccessful(null));
        assertThat(exception.getMessage(), is(equalTo(HttpExecutor.NULL_HTTP_RESPONSE_ERROR_MESSAGE)));
    }

    @Test
    @DisplayName("throwExceptionIfNotSuccessful throws Exception when status code is not successful")
    public void throwExceptionIfNotSuccessfulThrowsExceptionWhenStatusCodeIsNotSuccessful() {
        assertThrows(FailedHttpRequestException.class,
            () -> executor.throwExceptionIfNotSuccessful(failingResponse));
    }

    @Test
    @DisplayName("throwExceptionIfNotSuccessful returns the status code in the Exception message")
    public void throwExceptionIfNotSuccessfulReturnsTheStatusCodeInTheExceptionMessage() {
        FailedHttpRequestException exception = assertThrows(FailedHttpRequestException.class,
            () -> executor.throwExceptionIfNotSuccessful(failingResponse));
        assertThat(exception.getMessage(), containsString(ERROR_CODE.toString()));
    }

    @Test
    @DisplayName("throwExceptionIfNotSuccessful returns the response body in the Exception message")
    public void throwExceptionIfNotSuccessfulReturnsTheResponseBodyInTheExceptionMessage() {
        FailedHttpRequestException exception = assertThrows(FailedHttpRequestException.class,
            () -> executor.throwExceptionIfNotSuccessful(failingResponse));
        assertThat(exception.getMessage(), containsString(ERROR_BODY));
    }

    @Test
    @DisplayName("throwExceptionIfNotSuccessful throws Exception when status code is informational (1xx)")
    public void throwExceptionIfNotSuccessfulThrowsExceptionWhenStatusCodeIsInformational() {
        assertThrows(FailedHttpRequestException.class,
            () -> executor.throwExceptionIfNotSuccessful(informationResponse));
    }
}