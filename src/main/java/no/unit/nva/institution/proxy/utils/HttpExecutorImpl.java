package no.unit.nva.institution.proxy.utils;

import no.unit.nva.institution.proxy.HttpExecutor;
import no.unit.nva.institution.proxy.InstitutionListResponse;
import nva.commons.utils.attempt.Try;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static nva.commons.utils.attempt.Try.attempt;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.USER_AGENT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class HttpExecutorImpl implements HttpExecutor {

    public static final String NVA_INSTITUTIONS_LIST_CRAWLER = "NVA Institutions List Crawler";
    public static final String INSTITUTIONS_URI_TEMPLATE =
            "https://api.cristin.no/v2/institutions?country=NO&per_page=1000000&lang=%s";
    private final HttpClient httpClient;

    public HttpExecutorImpl() {
        httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }
    @Override
    public InstitutionListResponse getInstitutions(Language language) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .header(ACCEPT, APPLICATION_JSON.getMimeType())
                .header(USER_AGENT, NVA_INSTITUTIONS_LIST_CRAWLER)
                .uri(URI.create(generateInstitutionsQueryUri(language)))
                .build();
        Try<InstitutionListResponse> response = httpClient.sendAsync(httpRequest,
                HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(attempt(this::toInstitutionListResponse))
                .get();

        // this needs to be analysed in QA
        if (response.isSuccess()) {
            return response.get();
        } else {
            throw response.getException();
        }
    }

    private InstitutionListResponse toInstitutionListResponse(String institutionDto) throws IOException {
        return InstitutionUtils.toInstitutionListResponse(institutionDto);
    }
    private String generateInstitutionsQueryUri(Language language) {
        return String.format(INSTITUTIONS_URI_TEMPLATE, language.getCode());
    }
}
