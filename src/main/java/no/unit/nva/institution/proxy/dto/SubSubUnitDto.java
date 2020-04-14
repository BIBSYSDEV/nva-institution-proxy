package no.unit.nva.institution.proxy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SubSubUnitDto {
    private String id;
    private Map<String, String> unitName;
    private InstitutionDto institution;
    private InstitutionDto parentUnit;
    private List<InstitutionDto> parentUnits;
    private SubUnitDto[] subUnits;

    public SubSubUnitDto(@JsonProperty("cristin_unit_id")  String id,
                         @JsonProperty("unit_name") Map<String, String> unitName,
                         @JsonProperty("institution") InstitutionDto institution,
                         @JsonProperty("parent_unit") InstitutionDto parentUnit,
                         @JsonProperty("parent_units") List<InstitutionDto> parentUnits,
                         @JsonProperty("subunits") SubUnitDto[] subUnits) {
        this.id = id;
        this.unitName = unitName;
        this.institution = institution;
        this.parentUnit = parentUnit;
        this.parentUnits = parentUnits;
        this.subUnits = subUnits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getUnitName() {
        return unitName;
    }

    public void setUnitName(Map<String, String> unitName) {
        this.unitName = unitName;
    }

    public InstitutionDto getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDto institution) {
        this.institution = institution;
    }

    public InstitutionDto getParentUnit() {
        return parentUnit;
    }

    public void setParentUnit(InstitutionDto parentUnit) {
        this.parentUnit = parentUnit;
    }

    public List<InstitutionDto> getParentUnits() {
        return parentUnits;
    }

    public void setParentUnits(List<InstitutionDto> parentUnits) {
        this.parentUnits = parentUnits;
    }

    public List<SubUnitDto> getSubUnits() {
        return new ArrayList<>(Arrays.asList(subUnits));
    }

    public void setSubUnits(SubUnitDto[] subUnits) {
        this.subUnits = subUnits;
    }
}
