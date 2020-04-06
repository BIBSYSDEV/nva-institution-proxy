package no.unit.nva.institution.proxy;

import static nva.commons.utils.attempt.Try.attempt;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.USER_AGENT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import no.unit.nva.institution.proxy.exception.InstitutionFailureException;
import no.unit.nva.institution.proxy.utils.InstitutionUtils;
import no.unit.nva.institution.proxy.utils.Language;
import nva.commons.exceptions.ApiGatewayException;
import nva.commons.utils.JacocoGenerated;

public class HttpExecutorImpl implements HttpExecutor {

    public static final String NVA_INSTITUTIONS_LIST_CRAWLER = "NVA Institutions List Crawler";
    public static final String INSTITUTIONS_URI_TEMPLATE =
        "https://api.cristin.no/v2/institutions?country=NO&per_page=1000000&lang=%s";

    private final HttpClient httpClient;

    @JacocoGenerated
    public HttpExecutorImpl() {
        this(HttpClient.newBuilder()
                       .followRedirects(HttpClient.Redirect.ALWAYS)
                       .connectTimeout(Duration.ofSeconds(30))
                       .build());
    }

    public HttpExecutorImpl(HttpClient client) {
        this.httpClient = client;
    }

    private CompletableFuture<HttpResponse<String>> sendHttRequest(Language language) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                                             .GET()
                                             .header(ACCEPT, APPLICATION_JSON.getMimeType())
                                             .header(USER_AGENT, NVA_INSTITUTIONS_LIST_CRAWLER)
                                             .uri(URI.create(generateInstitutionsQueryUri(language)))
                                             .build();
        return httpClient.sendAsync(httpRequest, BodyHandlers.ofString());
    }

    @Override
    public InstitutionListResponse getInstitutions(Language language)
        throws ApiGatewayException {

        return
            attempt(() -> sendHttRequest(language).get())
                .map(this::throwExceptionIfNotSuccessful)
                .map(HttpResponse::body)
                .map(this::toInstitutionListResponse)
                .orElseThrow(resp -> handleError(resp.getException()));
    }

    private InstitutionFailureException handleError(Exception exception) {
        return new InstitutionFailureException(exception);
    }

    private InstitutionListResponse toInstitutionListResponse(String institutionDto) throws IOException {
        return InstitutionUtils.toInstitutionListResponse(institutionDto);
    }

    private String generateInstitutionsQueryUri(Language language) {
        return String.format(INSTITUTIONS_URI_TEMPLATE, language.getCode());
    }
}
