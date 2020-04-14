package no.unit.nva.institution.proxy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public class CorrespondingUnitDto {
    private String id;
    private URI uri;

    public CorrespondingUnitDto(@JsonProperty("cristin_unit_id") String id, @JsonProperty("url") String uri) {
        this.id = id;
        this.uri = URI.create(uri);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
