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

    /**
     * Construrctor for internal usage.
     *
     * @param uri      a URI string.
     * @param language A language code.
     */
    public NestedInstitutionRequest(String uri, String language) {
        this.uri = uri;
        this.language = language;
    }

    @JacocoGenerated
    public String getUri() {
        return uri;
    }

    @JacocoGenerated
    public String getLanguage() {
        return language;
    }
}
