package no.unit.nva.institution.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import nva.commons.utils.JacocoGenerated;

public class NestedInstitutionResponse {
    @JsonProperty("json")
    private String json;

    @JacocoGenerated
        /* default */ NestedInstitutionResponse() {
    }

    public NestedInstitutionResponse(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }
}
