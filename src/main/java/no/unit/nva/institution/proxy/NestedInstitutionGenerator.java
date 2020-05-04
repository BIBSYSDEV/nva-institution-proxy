package no.unit.nva.institution.proxy;

import static no.unit.nva.institution.proxy.utils.MapUtils.getNameValue;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import no.unit.nva.institution.proxy.dto.SubSubUnitDto;
import no.unit.nva.institution.proxy.utils.ModelUtils;

public class NestedInstitutionGenerator {

    private final ModelUtils modelUtils;

    public NestedInstitutionGenerator() {
        this.modelUtils = new ModelUtils();
    }

    /**
     * Returns a JSON-LD string representing a nested institution.
     *
     * @return A JSON-LD string
     */
    public JsonNode getNestedInstitution() {
        return modelUtils.toJsonLd();
    }

    /**
     * Adds a unit to the model, creating a triple for the name and a triple for the parent relation.
     *
     * @param uri           The URI of the unit
     * @param subSubUnitDto The object representing the unit
     */
    public void addUnitToModel(URI uri, SubSubUnitDto subSubUnitDto) {
        modelUtils.addNameToModel(uri, getNameValue(subSubUnitDto.getUnitName()));
        modelUtils.addSubunitsRelationToModel(subSubUnitDto.getParentUnit().getUri(), uri);
    }

    /**
     * Adds a single triple for the top-level institution, using the corresponding unit URI as subject.
     *
     * @param uri  The URI of the corresponding unit
     * @param name The name of the unit.
     */
    public void setInstitution(URI uri, String name) {
        modelUtils.addTypeToModel(uri);
        modelUtils.addNameToModel(uri, name);
    }
}
