package no.unit.nva.institution.proxy.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class representing a call to <pre>https://api.cristin.no/v2/institutions/</pre>. It represents an "Institution" unit.
 * Examples of such institutions are NTNU, UIO etc. The corresponding unit is the organizational unit tha corresponds to
 * this institution.
 */
public class InstitutionBaseDto {

    /*
    /*
{
  "cristin_institution_id" : "185",
  "institution_name" : {
    "en" : "University of Oslo"
  },
  "acronym" : "UIO",
  "country" : "NO",
  "cristin_user_institution" : true,
  "corresponding_unit" : {
    "cristin_unit_id" : "185.90.0.0",
    "url" : "https://api.cristin.no/v2/units/185.90.0.0"
  }
}
 */
    @JsonAlias("cristin_unit_id")
    private String id;
    @JsonAlias("unit_name")
    private Map<String, String> name;
    private String shortName;
    private String country;
    private boolean cristinUser;
    private CorrespondingUnitDto correspondingUnitDto;
    private SubUnitDto[] subUnitDtoList;
    private InstitutionDto institution;

    @JsonCreator
    public InstitutionBaseDto(@JsonProperty("institution_name") Map<String, String> name,
                              @JsonProperty("institution") InstitutionDto institution,
                              @JsonProperty("cristin_user_institution") boolean cristinUser,
                              @JsonProperty("cristin_institution_id") String id,
                              @JsonProperty("acronym") String shortName,
                              @JsonProperty("country") String country,
                              @JsonProperty("corresponding_unit") CorrespondingUnitDto correspondingUnitDto,
                              @JsonProperty("subunits")
                                  SubUnitDto[] subUnitDtoList) {
        this.name = name;
        this.institution = institution;
        this.cristinUser = cristinUser;
        this.id = id;
        this.shortName = shortName;
        this.country = country;
        this.correspondingUnitDto = correspondingUnitDto;
        this.subUnitDtoList = subUnitDtoList;
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
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

    public CorrespondingUnitDto getCorrespondingUnitDto() {
        return correspondingUnitDto;
    }

    public void setCorrespondingUnitDto(CorrespondingUnitDto correspondingUnitDto) {
        this.correspondingUnitDto = correspondingUnitDto;
    }

    public List<SubUnitDto> getSubUnitDtoList() {
        return new ArrayList<>(Arrays.asList(subUnitDtoList));
    }

    public void setSubUnitDtoList(SubUnitDto[] subUnitDtoList) {
        this.subUnitDtoList = subUnitDtoList;
    }

    public InstitutionDto getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDto institution) {
        this.institution = institution;
    }
}
