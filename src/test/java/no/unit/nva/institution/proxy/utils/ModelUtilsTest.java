package no.unit.nva.institution.proxy.utils;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import static nva.commons.utils.IoUtils.stringFromResources;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

class ModelUtilsTest {

    public static final String INSTITUTION_URI = "https://example.org/institution";
    public static final String MODEL_UTILS_DATA = "model_utils_data";
    public static final Path HAS_TYPE_JSON = Path.of(MODEL_UTILS_DATA,"has_type.json");
    public static final String SOME_NAME = "Some Name";
    public static final Path HAS_NAME_JSON = Path.of(MODEL_UTILS_DATA, "has_name.json");
    public static final String SUBUNIT_URI = "https://example.org/subunit";
    private static final Path HAS_SUBUNIT_JSON = Path.of(MODEL_UTILS_DATA, "has_subunit.json");

    @DisplayName("ModelUtils constructor exists")
    @Test
    void modelUtilsConstructorExists() {
        new ModelUtils();
    }

    @DisplayName("Model utils can add a type for a URI to a model")
    @Test
    void addTypeToModelWithUriCreatesInstitutionObject() throws IOException {
        ModelUtils modelUtils = new ModelUtils();
        URI uri = URI.create(INSTITUTION_URI);
        modelUtils.addTypeToModel(uri);
        assertThat(modelUtils.toJsonLd(), containsString(stringFromResources(HAS_TYPE_JSON)));
    }

    @DisplayName("Model utils can add name statement for a URI to model")
    @Test
    void addNameStatementWithUriCreatesNameStatement() throws IOException {
        ModelUtils modelUtils = new ModelUtils();
        URI uri = URI.create(INSTITUTION_URI);

        // For the frame to work, there must be a node of type Institution
        modelUtils.addNameToModel(uri, SOME_NAME);
        modelUtils.addTypeToModel(uri);
        assertThat(modelUtils.toJsonLd(), containsString(stringFromResources(HAS_NAME_JSON)));
    }


    @DisplayName("Model utils can add subunit relation to an object")
    @Test
    void addSubunitsRelationToModelWithUriCreatesSubunitRelation() throws IOException {
        ModelUtils modelUtils = new ModelUtils();
        URI institution = URI.create(INSTITUTION_URI);
        URI subunit = URI.create(SUBUNIT_URI);
        modelUtils.addTypeToModel(institution);
        modelUtils.addSubunitsRelationToModel(institution, subunit);
        assertThat(modelUtils.toJsonLd(), containsString(stringFromResources(HAS_SUBUNIT_JSON)));
    }
}