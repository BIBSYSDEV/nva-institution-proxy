package no.unit.nva.institution.proxy.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import nva.commons.utils.JacocoGenerated;

public class NestedInstitutionRequest {

    @JsonProperty("uri")
    private String uri;
    @JsonProperty("language")
    private String language;



    /**
     * Necessary for JsonJackson.
     */
    @JacocoGenerated
    public NestedInstitutionRequest() {
    }

    public NestedInstitutionRequest(String uri, String language) {
        this.uri = uri;
        this.language = language;
    }

    public String getUri() {
        return uri;
    }

    public String getLanguage() {
        return language;
    }

}
