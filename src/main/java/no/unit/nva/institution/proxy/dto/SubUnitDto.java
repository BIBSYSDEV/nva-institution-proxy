package no.unit.nva.institution.proxy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.Map;

public class SubUnitDto {
    /*
    {
    "cristin_unit_id" : "185.11.0.0",
    "unit_name" : {
      "en" : "Faculty of Theology"
    },
    "url" : "https://api.cristin.no/v2/units/185.11.0.0"
  }
     */



    private String id;
    private Map<String, String> name;
    private Map<String, String> institution;
    private URI uri;
    private String acronym;

    public SubUnitDto(@JsonProperty("cristin_unit_id") String id,
                      @JsonProperty("unit_name") Map<String, String> name,
                      @JsonProperty("institution") Map<String, String> institution,
                      @JsonProperty("url") String uri,
                      @JsonProperty("acronym") String acronym) {
        this.id = id;
        this.name = name;
        this.institution = institution;
        this.uri = URI.create(uri);
        this.acronym = acronym;
    }

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

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Map<String, String> getInstitution() {
        return institution;
    }

    public void setInstitution(Map<String, String> institution) {
        this.institution = institution;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }
}
