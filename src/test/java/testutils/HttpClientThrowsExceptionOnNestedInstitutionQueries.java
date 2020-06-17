package testutils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static testutils.HttpClientGetsNestedInstitutionResponse.ADMINISTRATION_ONE_LH_URI;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.utils.Language;
import org.mockito.invocation.InvocationOnMock;

public class HttpClientThrowsExceptionOnNestedInstitutionQueries {

    private final HttpClient clientForNestedQueries;
    private final HttpClient client;
    private String errorMessage;

    /**
     * HttpClient that throws error when querying for a Subsubunit.
     *
     * @param language a language parameter.
     * @throws InvalidUriException when a URI is invalid.
     */
    public HttpClientThrowsExceptionOnNestedInstitutionQueries(Language language) throws InvalidUriException {
        clientForNestedQueries = new HttpClientGetsNestedInstitutionResponse(language).getMockClient();
        client = mock(HttpClient.class);
        setup();
    }

    private void setup() {
        when(client.sendAsync(any(HttpRequest.class), any(BodyHandler.class)))
            .thenAnswer(this::manageReturnedResponse);
    }

    private CompletableFuture<HttpResponse<String>> manageReturnedResponse(InvocationOnMock invocation)
        throws ExecutionException, InterruptedException {
        HttpRequest request = invocation.getArgument(0);
        if (requestShouldFail(request)) {
            return futureThrowingException(request.uri());
        } else {
            return returnSuccessfulResponse(invocation);
        }
    }

    private boolean requestShouldFail(HttpRequest request) {
        return request.uri().toString().contains(ADMINISTRATION_ONE_LH_URI);
    }

    private CompletableFuture<HttpResponse<String>> returnSuccessfulResponse(InvocationOnMock invocation) {
        HttpRequest request = invocation.getArgument(0);
        BodyHandler<String> handler = invocation.getArgument(1);
        return clientForNestedQueries.sendAsync(request, handler);
    }

    private CompletableFuture<HttpResponse<String>> futureThrowingException(URI uri)
        throws ExecutionException, InterruptedException {
        CompletableFuture<HttpResponse<String>> mockFuture = mock(CompletableFuture.class);
        setErrorMessage(uri.toString());
        when(mockFuture.get()).thenThrow(new ExecutionException(new IOException(getErrorMessage())));
        return mockFuture;
    }

    public HttpClient getMockClient() {
        return client;
    }

    private void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
