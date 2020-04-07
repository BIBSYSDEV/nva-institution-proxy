package no.unit.nva.institution.proxy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.Map;

public class InstitutionDto {
    /*
    {
      "cristin_institution_id" : "1",
      "institution_name" : {
        "nb" : "Privatperson"
      },
      "acronym" : "PRIVAT",
      "country" : "NO",
      "cristin_user_institution" : false,
      "url" : "https://api.cristin.no/v2/institutions/1"
    }
     */

    @JsonProperty("cristin_institution_id")
    private String id;
    @JsonProperty("institution_name")
    private Map<String, String> name;
    @JsonProperty("acronym")
    private String acronym;
    @JsonProperty("country")
    private String country;
    @JsonProperty("cristin_user_institution")
    private boolean cristinUser;
    @JsonProperty("url")
    private URI uri;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isCristinUser() {
        return cristinUser;
    }

    public void setCristinUser(boolean cristinUser) {
        this.cristinUser = cristinUser;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
