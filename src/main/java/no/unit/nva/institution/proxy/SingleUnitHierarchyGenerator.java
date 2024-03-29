package no.unit.nva.institution.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import no.unit.nva.institution.proxy.dto.InstitutionDto;
import no.unit.nva.institution.proxy.dto.SubSubUnitDto;
import no.unit.nva.institution.proxy.exception.HttpClientFailureException;
import no.unit.nva.institution.proxy.exception.NonExistingUnitError;
import no.unit.nva.institution.proxy.utils.Language;
import no.unit.nva.institution.proxy.utils.MapUtils;
import no.unit.nva.institution.proxy.utils.ModelUtils;
import no.unit.nva.institution.proxy.utils.UriUtils;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.JsonUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.util.Objects.nonNull;

public class SingleUnitHierarchyGenerator {

    private final ModelUtils modelUtils;
    private final HttpClient httpClient;

    private final Logger logger = LoggerFactory.getLogger(SingleUnitHierarchyGenerator.class);

    @JacocoGenerated
    public SingleUnitHierarchyGenerator(URI uri, Language language)
        throws InterruptedException, NonExistingUnitError, HttpClientFailureException {
        this(uri, language, newHttpClient());
    }

    /**
     * Parametrized constructor.
     *
     * @param uri        the URI of the Crstin Unit
     * @param language   the language we want the information in.
     * @param httpClient an {@link HttpClient}
     * @throws InterruptedException       when the client throws such exception.
     * @throws NonExistingUnitError       when the URI does not correspond to an existing unit.
     * @throws HttpClientFailureException when HttpClient receives an error.
     */
    public SingleUnitHierarchyGenerator(URI uri, Language language, HttpClient httpClient)
        throws InterruptedException, NonExistingUnitError, HttpClientFailureException {
        this.modelUtils = new ModelUtils();
        this.httpClient = httpClient;
        fetchHierarchy(uri, language);
    }

    @JacocoGenerated
    private static HttpClient newHttpClient() {
        return HttpClient.newHttpClient();
    }

    public JsonNode toJsonLd() {
        return this.modelUtils.toJsonLd();
    }

    private void fetchHierarchy(URI uri, Language language)
        throws InterruptedException, NonExistingUnitError, HttpClientFailureException {
        SubSubUnitDto current = fetchAndUpdateModel(uri, language);
        URI parent = Optional.ofNullable(current.getParentUnit())
            .map(InstitutionDto::getUri).orElse(null);
        while (nonNull(current.getParentUnit())) {
            current = fetchAndUpdateModel(parent, language);
            if (nonNull(current.getParentUnit())) {
                parent = current.getParentUnit().getUri();
            }
        }
    }

    private SubSubUnitDto fetchAndUpdateModel(URI uri, Language language)
        throws InterruptedException, NonExistingUnitError, HttpClientFailureException {
        URI uriWithoutParameters = UriUtils.clearParameters(uri);
        SubSubUnitDto current = fetch(UriUtils.getUriWithLanguage(uri, language));

        modelUtils.addNameToModel(uriWithoutParameters, MapUtils.getNameValue(current.getUnitName()));
        if (nonNull(current.getParentUnit())) {
            modelUtils.addSubunitsRelationToModel(current.getParentUnit().getUri(), uriWithoutParameters);
        } else {
            modelUtils.addTypeToModel(uriWithoutParameters);
        }
        return current;
    }

    private SubSubUnitDto fetch(URI uri)
        throws InterruptedException, NonExistingUnitError, HttpClientFailureException {

        HttpRequest httpRequest = createHttpRequest(uri);
        HttpResponse<String> response = sendRequest(httpRequest);
        if (isSuccessful(response.statusCode())) {
            return toUnit(response.body());
        } else {
            throw new NonExistingUnitError(uri.toString());
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest httpRequest) throws InterruptedException,
                                                                             HttpClientFailureException {
        try {
            return httpClient.sendAsync(httpRequest, BodyHandlers.ofString()).get();
        } catch (ExecutionException e) {
            throw new HttpClientFailureException(e);
        }
    }

    private boolean isSuccessful(int statusCode) {
        return statusCode <= HttpStatus.SC_MULTIPLE_CHOICES && statusCode >= HttpStatus.SC_OK;
    }

    private SubSubUnitDto toUnit(String json) {
        try {
            return JsonUtils.dtoObjectMapper.readValue(json, SubSubUnitDto.class);
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON string: " + json, e);
        }
        return null;
    }

    private HttpRequest createHttpRequest(URI uri) {
        return HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .build();
    }
}
