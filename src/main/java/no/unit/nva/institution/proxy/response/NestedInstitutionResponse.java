package no.unit.nva.institution.proxy.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import nva.commons.utils.JacocoGenerated;

public class NestedInstitutionResponse {

    @JsonProperty("json")
    private JsonNode json;

    @JacocoGenerated
        /* default */ NestedInstitutionResponse() {
    }

    public NestedInstitutionResponse(JsonNode json) {
        this.json = json;
    }

    public JsonNode getJson() {
        return json;
    }
}
