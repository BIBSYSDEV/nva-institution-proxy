package no.unit.nva.institution.proxy.utils;

import static nva.commons.utils.IoUtils.stringFromResources;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ModelUtilsTest {

    public static final String INSTITUTION_URI = "https://example.org/institution";
    public static final String MODEL_UTILS_DATA = "model_utils_data";
    public static final Path HAS_TYPE_JSON = Path.of(MODEL_UTILS_DATA,"has_type.json");
    public static final String SOME_NAME = "Some Name";
    public static final Path HAS_NAME_JSON = Path.of(MODEL_UTILS_DATA, "has_name.json");
    public static final String SUBUNIT_URI = "https://example.org/subunit";
    private static final Path HAS_SUBUNIT_JSON = Path.of(MODEL_UTILS_DATA, "has_subunit.json");
    private static final ObjectMapper MAPPER = new ObjectMapper();

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
        Object resultModel = objectify(modelUtils.toJsonLd());
        Object expectedModel = objectify(stringFromResources(HAS_TYPE_JSON));
        assertThat(resultModel, is(equalTo(expectedModel)));
    }

    private Object objectify(String json) throws com.fasterxml.jackson.core.JsonProcessingException {
        return MAPPER.readValue(json, Object.class);
    }

    @DisplayName("Model utils can add name statement for a URI to model")
    @Test
    void addNameStatementWithUriCreatesNameStatement() throws IOException {
        ModelUtils modelUtils = new ModelUtils();
        URI uri = URI.create(INSTITUTION_URI);

        // For the frame to work, there must be a node of type Institution
        modelUtils.addNameToModel(uri, SOME_NAME);
        modelUtils.addTypeToModel(uri);
        assertThat(objectify(modelUtils.toJsonLd()), is(equalTo(objectify(stringFromResources(HAS_NAME_JSON)))));
    }

    @DisplayName("Model utils can add subunit relation to an object")
    @Test
    void addSubunitsRelationToModelWithUriCreatesSubunitRelation() throws IOException {
        ModelUtils modelUtils = new ModelUtils();
        URI institution = URI.create(INSTITUTION_URI);
        URI subunit = URI.create(SUBUNIT_URI);
        modelUtils.addTypeToModel(institution);
        modelUtils.addSubunitsRelationToModel(institution, subunit);
        assertThat(objectify(modelUtils.toJsonLd()), is(equalTo(objectify(stringFromResources(HAS_SUBUNIT_JSON)))));
    }

    @DisplayName("toTurtle returns a non empty string when the input is a non empty model")
    @Test
    public void toTurtleReturnsANonEmptyStringWhenTheInputIsNotEmpty() {
        ModelUtils modelUtils = new ModelUtils();
        URI uri = URI.create(INSTITUTION_URI);
        modelUtils.addNameToModel(uri, SOME_NAME);
        String actual = modelUtils.toTurtle();
        assertNotNull(actual);
    }
}