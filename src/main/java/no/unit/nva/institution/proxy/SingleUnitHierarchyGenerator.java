package no.unit.nva.institution.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.institution.proxy.dto.InstitutionDto;
import no.unit.nva.institution.proxy.dto.SubSubUnitDto;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.NonExistingUnitError;
import no.unit.nva.institution.proxy.utils.Language;
import no.unit.nva.institution.proxy.utils.MapUtils;
import no.unit.nva.institution.proxy.utils.ModelUtils;
import no.unit.nva.institution.proxy.utils.UriUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class SingleUnitHierarchyGenerator {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ModelUtils modelUtils;

    SingleUnitHierarchyGenerator(URI uri, Language language) throws ExecutionException, InterruptedException,
            NonExistingUnitError, InvalidUriException {
        this.modelUtils = new ModelUtils();
        fetchHierarchy(uri, language);
    }

    public String toJsonLd() {
        return this.modelUtils.toJsonLd();
    }

    private void fetchHierarchy(URI uri, Language language) throws ExecutionException, InterruptedException,
            NonExistingUnitError, InvalidUriException {
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

    private SubSubUnitDto fetchAndUpdateModel(URI uri, Language language) throws ExecutionException, InterruptedException,
            NonExistingUnitError, InvalidUriException {
        SubSubUnitDto current = fetch(UriUtils.getUriWithLanguage(uri, language));
        if (isNull(current)) {
            throw new NonExistingUnitError(uri.toString());
        }
        modelUtils.addNameToModel(uri, MapUtils.getNameValue(current.getUnitName()));
        if (nonNull(current.getParentUnit())) {
            modelUtils.addSubunitsRelationToModel(current.getParentUnit().getUri(), uri);
        } else {
            modelUtils.addTypeToModel(uri);
        }
        return current;
    }

    private SubSubUnitDto fetch(URI uri) throws ExecutionException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest httpRequest = createHttpRequest(uri);
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::toUnit)
                .get();
    }


    private SubSubUnitDto toUnit(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, SubSubUnitDto.class);
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
