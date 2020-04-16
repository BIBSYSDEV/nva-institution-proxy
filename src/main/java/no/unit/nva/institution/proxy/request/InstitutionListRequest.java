package no.unit.nva.institution.proxy.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import nva.commons.utils.JacocoGenerated;

public class InstitutionListRequest {

    @JsonProperty("language")
    private String language;

    /**
     * Necessary for JsonJackson.
     */
    @JacocoGenerated
    public InstitutionListRequest() {
    }

    private InstitutionListRequest(Builder builder) {
        setLanguage(builder.language);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public static final class Builder {

        private String language;

        public Builder() {
        }

        public Builder withLanguage(String language) {
            this.language = language;
            return this;
        }

        public InstitutionListRequest build() {
            return new InstitutionListRequest(this);
        }
    }
}
