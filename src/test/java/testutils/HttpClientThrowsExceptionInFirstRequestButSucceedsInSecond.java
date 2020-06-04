package testutils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.utils.Language;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

public class HttpClientThrowsExceptionInFirstRequestButSucceedsInSecond {

    public static final String SOME_MESSAGE = "Some message";
    private HttpClient client = Mockito.mock(HttpClient.class);

    private HttpClient clientForNestedQueries;

    private int requestCounter = 0;

    public HttpClientThrowsExceptionInFirstRequestButSucceedsInSecond()
        throws InterruptedException, ExecutionException, InvalidUriException {
        setup();
    }

    private void setup() throws InvalidUriException {
        clientForNestedQueries = new HttpClientGetsNestedInstitutionResponse(Language.ENGLISH)
            .getMockClient();

        when(client.sendAsync(any(HttpRequest.class), any(BodyHandler.class)))
            .thenAnswer(this::manageReturnedResponse);
    }

    private CompletableFuture<HttpResponse<String>> manageReturnedResponse(InvocationOnMock invocation)
        throws ExecutionException, InterruptedException {
        if (requestCounter == 0) {
            prepareToReturnSuccessOnNextCall();
            return futureThrowingException();
        } else {
            return returnSuccessfulResponse(invocation);
        }
    }

    private void prepareToReturnSuccessOnNextCall() {
        requestCounter++;
    }

    private CompletableFuture<HttpResponse<String>> returnSuccessfulResponse(InvocationOnMock invocation) {
        HttpRequest request = invocation.getArgument(0);
        BodyHandler<String> handler = invocation.getArgument(1);
        return clientForNestedQueries.sendAsync(request, handler);
    }

    private CompletableFuture<HttpResponse<String>> futureThrowingException()
        throws ExecutionException, InterruptedException {
        CompletableFuture<HttpResponse<String>> mockFuture = mock(CompletableFuture.class);
        when(mockFuture.get()).thenThrow(new ExecutionException(new IOException(SOME_MESSAGE)));
        return mockFuture;
    }

    public HttpClient getClient() {
        return client;
    }
}
