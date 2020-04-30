package no.unit.nva.institution.proxy;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import no.unit.nva.institution.proxy.dto.InstitutionDto;
import no.unit.nva.institution.proxy.dto.SubSubUnitDto;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.JsonParsingException;
import no.unit.nva.institution.proxy.exception.NonExistingUnitError;
import no.unit.nva.institution.proxy.utils.Language;
import no.unit.nva.institution.proxy.utils.MapUtils;
import no.unit.nva.institution.proxy.utils.ModelUtils;
import no.unit.nva.institution.proxy.utils.UriUtils;
import nva.commons.utils.JacocoGenerated;
import nva.commons.utils.JsonUtils;
import org.apache.http.HttpStatus;

public class SingleUnitHierarchyGenerator {

    private final ModelUtils modelUtils;
    private final HttpClient httpClient;

    @JacocoGenerated
    public SingleUnitHierarchyGenerator(URI uri, Language language)
        throws InterruptedException, NonExistingUnitError, GatewayException {
        this(uri, language, newHttpClient());
    }

    /**
     * Parametrized constructor.
     *
     * @param uri        the URI of the Crstin Unit
     * @param language   the language we want the information in.
     * @param httpClient an {@link HttpClient}
     * @throws InterruptedException when the client throws such exception.
     * @throws NonExistingUnitError when the URI does not correspond to an existing unit.
     * @throws GatewayException     when HttpClient receives an error.
     */
    public SingleUnitHierarchyGenerator(URI uri, Language language, HttpClient httpClient)
        throws InterruptedException, NonExistingUnitError, GatewayException {
        this.modelUtils = new ModelUtils();
        this.httpClient = httpClient;
        fetchHierarchy(uri, language);
    }

    @JacocoGenerated
    private static HttpClient newHttpClient() {
        return HttpClient.newHttpClient();
    }

    public JsonNode toJsonLd() throws JsonParsingException {
        return this.modelUtils.toJsonLd();
    }

    private void fetchHierarchy(URI uri, Language language)
        throws InterruptedException, NonExistingUnitError, GatewayException {
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
        throws InterruptedException, NonExistingUnitError, GatewayException {
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
        throws InterruptedException, NonExistingUnitError, GatewayException {

        HttpRequest httpRequest = createHttpRequest(uri);
        HttpResponse<String> response = sendRequest(httpRequest);
        if (isSuccessful(response.statusCode())) {
            return toUnit(response.body());
        } else {
            throw new NonExistingUnitError(uri.toString());
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest httpRequest) throws InterruptedException, GatewayException {
        try {
            return httpClient.sendAsync(httpRequest, BodyHandlers.ofString()).get();
        } catch (ExecutionException e) {
            throw new GatewayException(e);
        }
    }

    private boolean isSuccessful(int statusCode) {
        return statusCode <= HttpStatus.SC_MULTIPLE_CHOICES && statusCode >= HttpStatus.SC_OK;
    }

    private SubSubUnitDto toUnit(String json) {
        try {
            return JsonUtils.objectMapper.readValue(json, SubSubUnitDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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
