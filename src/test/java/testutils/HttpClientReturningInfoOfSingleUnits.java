package testutils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.PushPromiseHandler;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import no.unit.nva.institution.proxy.utils.Language;
import nva.commons.utils.IoUtils;
import org.apache.http.HttpStatus;

/**
 * Class mocking an HttpClient. The class returns JSON strings for some predefined URIs.
 */
public class HttpClientReturningInfoOfSingleUnits extends HttpClient {

    public static final URI ROOT_NODE_URI = URI.create("https://api.cristin.no/v2/units/185.90.0.0?lang=en");
    public static final URI FIRST_LEVEL_CHILD_URI = URI.create("https://api.cristin.no/v2/units/185.15.0.0?lang=en");
    public static final URI SECOND_LEVEL_CHILD_URI = URI.create("https://api.cristin.no/v2/units/185.15.3.0?lang=en");

    public static final URI NON_EXISTING_URI = URI.create("https://api.cristin.no/v2/units/185.185.252.252?lang=en");

    public static final String CRISTIN_RESOURCES = "cristin_responses";
    public static final Path UNIT_WITH_NO_PARENT = Path.of(CRISTIN_RESOURCES, "unit_with_no_parent.json");
    public static final Path FIRST_LEVEL_CHILD = Path.of(CRISTIN_RESOURCES, "unit_with_one_parent.json");
    public static final Path SECOND_LEVEL_CHILD = Path.of(CRISTIN_RESOURCES, "unit_with_two_parents.json");

    public static final Language TESTING_LANGUAGE = Language.ENGLISH;

    private Map<URI, String> requestResponseMap;

    public HttpClientReturningInfoOfSingleUnits() {
        setup();
    }

    /**
     * Setup.
     */
    public void setup() {
        requestResponseMap = new HashMap<>();
        requestResponseMap.put(ROOT_NODE_URI, readResource(UNIT_WITH_NO_PARENT));
        requestResponseMap.put(FIRST_LEVEL_CHILD_URI, readResource(FIRST_LEVEL_CHILD));
        requestResponseMap.put(SECOND_LEVEL_CHILD_URI, readResource(SECOND_LEVEL_CHILD));
    }

    private HttpResponse<String> createResponse(String responseString) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn(responseString);
        when(response.statusCode()).thenReturn(HttpStatus.SC_OK);
        return response;
    }

    private static String readResource(Path resourcePath) {
        return IoUtils.stringFromResources(resourcePath);
    }

    @Override
    public <T> HttpResponse<T> send(HttpRequest request, BodyHandler<T> responseBodyHandler) throws IOException {
        try {
            return sendAsync(request, responseBodyHandler).get();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, BodyHandler<T> responseBodyHandler) {
        URI requestUri = request.uri();
        if (requestResponseMap.containsKey(requestUri)) {
            HttpResponse<T> response = (HttpResponse<T>) createResponse(requestResponseMap.get(requestUri));
            return CompletableFuture.completedFuture(response);
        } else {
            HttpResponse<T> response = mock(HttpResponse.class);
            when(response.statusCode()).thenReturn(HttpStatus.SC_NOT_FOUND);
            return CompletableFuture.completedFuture(response);
        }
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, BodyHandler<T> responseBodyHandler,
                                                            PushPromiseHandler<T> pushPromiseHandler) {
        return null;
    }

    @Override
    public Optional<CookieHandler> cookieHandler() {
        return Optional.empty();
    }

    @Override
    public Optional<Duration> connectTimeout() {
        return Optional.empty();
    }

    @Override
    public Redirect followRedirects() {
        return null;
    }

    @Override
    public Optional<ProxySelector> proxy() {
        return Optional.empty();
    }

    @Override
    public SSLContext sslContext() {
        return null;
    }

    @Override
    public SSLParameters sslParameters() {
        return null;
    }

    @Override
    public Optional<Authenticator> authenticator() {
        return Optional.empty();
    }

    @Override
    public Version version() {
        return null;
    }

    @Override
    public Optional<Executor> executor() {
        return Optional.empty();
    }
}
