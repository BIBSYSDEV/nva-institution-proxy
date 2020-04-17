package no.unit.nva.institution.proxy;

import no.unit.nva.institution.proxy.dto.InstitutionBaseDto;
import no.unit.nva.institution.proxy.dto.SubSubUnitDto;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.NonExistingUnitError;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.response.NestedInstitutionResponse;
import no.unit.nva.institution.proxy.utils.InstitutionUtils;
import no.unit.nva.institution.proxy.utils.Language;
import no.unit.nva.institution.proxy.utils.MapUtils;
import no.unit.nva.institution.proxy.utils.UriUtils;
import nva.commons.utils.JacocoGenerated;
import nva.commons.utils.attempt.Failure;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static nva.commons.utils.attempt.Try.attempt;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.USER_AGENT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;


public class HttpExecutorImpl extends HttpExecutor {

    public static final String NVA_INSTITUTIONS_LIST_CRAWLER = "NVA Institutions List Crawler";
    public static final String INSTITUTIONS_URI_TEMPLATE =
            "https://api.cristin.no/v2/institutions?country=NO&per_page=1000000&lang=%s";
    public static final String PARENT_UNIT_URI_TEMPLATE =
            "https://api.cristin.no/v2/units?parent_unit_id=%s&per_page=20000";
    private final HttpClient httpClient;

    /**
     * Default constructor.
     */
    @JacocoGenerated
    public HttpExecutorImpl() {
        this(HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(30))
                .build());
    }

    public HttpExecutorImpl(HttpClient client) {
        super();
        this.httpClient = client;
    }

    private CompletableFuture<HttpResponse<String>> sendHttpRequest(URI uri) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .header(ACCEPT, APPLICATION_JSON.getMimeType())
                .header(USER_AGENT, NVA_INSTITUTIONS_LIST_CRAWLER)
                .uri(uri)
                .build();
        return httpClient.sendAsync(httpRequest, BodyHandlers.ofString());
    }

    @Override
    public InstitutionListResponse getInstitutions(Language language) throws GatewayException {
        URI uri = URI.create(generateInstitutionsQueryUri(language));
        return attempt(() -> sendHttpRequest(uri).get())
            .map(this::throwExceptionIfNotSuccessful)
            .map(HttpResponse::body)
            .map(this::toInstitutionListResponse)
            .orElseThrow(this::handleError);
    }

    @Override
    public NestedInstitutionResponse getNestedInstitution(URI uri, Language language) throws
            GatewayException, InvalidUriException {
        URI unitUri = getInstitutionUnitUri(uri, language);
        InstitutionBaseDto institutionUnit = getInstitutionBaseDto(unitUri, language);

        String name = MapUtils.getNameValue(institutionUnit.getName());
        NestedInstitutionGenerator generator = new NestedInstitutionGenerator();
        generator.setInstitution(unitUri, name);
        List<URI> unitUris = getUnitUris(institutionUnit.getId(), language);

        for (URI subSubUnitUri : unitUris) {
            SubSubUnitDto subSubUnitDto = attempt(() -> getSubSubUnitDto(subSubUnitUri, language))
                .orElseThrow(this::handleError);
            generator.addUnitToModel(subSubUnitUri, subSubUnitDto);
        }
        return new NestedInstitutionResponse(generator.getNestedInstitution());
    }

    @Override
    public NestedInstitutionResponse getSingleUnit(URI uri, Language language) throws InterruptedException,
            ExecutionException, InvalidUriException, NonExistingUnitError {
        SingleUnitHierarchyGenerator singleUnitHierarchyGenerator = new SingleUnitHierarchyGenerator(uri, language);
        String json = singleUnitHierarchyGenerator.toJsonLd();
        return new NestedInstitutionResponse(json);
    }

    public URI getInstitutionUnitUri(URI uri, Language language) throws GatewayException {
        InstitutionBaseDto institutionDto = getInstitutionBaseDto(uri, language);
        return institutionDto.getCorrespondingUnitDto().getUri();
    }

    private SubSubUnitDto getSubSubUnitDto(URI subunitUri, Language language) throws GatewayException {
        return attempt(() -> sendHttpRequest(UriUtils.getUriWithLanguage(subunitUri, language)).get())
            .map(this::throwExceptionIfNotSuccessful)
            .map(HttpResponse::body)
            .map(InstitutionUtils::toSubSubUnitDto)
            .orElseThrow(this::handleError);
    }

    private List<URI> getUnitUris(String id, Language language) throws GatewayException, InvalidUriException {
        URI uri = UriUtils.getUriWithLanguage(URI.create(String.format(PARENT_UNIT_URI_TEMPLATE, id)), language);
        return attempt(() -> sendHttpRequest(uri).get())
            .map(this::throwExceptionIfNotSuccessful)
            .map(HttpResponse::body)
            .map(this::bodyToUriList)
            .orElseThrow(this::handleError);
    }

    private List<URI> bodyToUriList(String json) throws IOException {
        return InstitutionUtils.toUriList(json);
    }

    private InstitutionBaseDto getInstitutionBaseDto(URI uri, Language language) throws GatewayException {
        return attempt(() -> sendHttpRequest(UriUtils.getUriWithLanguage(uri, language)).get())
            .map(this::throwExceptionIfNotSuccessful)
            .map(HttpResponse::body)
            .map(this::toInstitutionBaseDto)
            .orElseThrow(this::handleError);
    }

    private InstitutionBaseDto toInstitutionBaseDto(String json) throws IOException {
        return InstitutionUtils.toInstitutionBaseDto(json);
    }

    private <T> GatewayException handleError(Failure<T> failure) {
        return new GatewayException(failure.getException());
    }

    private InstitutionListResponse toInstitutionListResponse(String institutionDto) throws IOException {
        return InstitutionUtils.toInstitutionListResponse(institutionDto);
    }

    private String generateInstitutionsQueryUri(Language language) {
        return String.format(INSTITUTIONS_URI_TEMPLATE, language.getCode());
    }
}
