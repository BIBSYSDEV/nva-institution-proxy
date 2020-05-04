package no.unit.nva.institution.proxy.utils;

import static nva.commons.utils.IoUtils.stringFromResources;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Path;
import no.unit.nva.institution.proxy.exception.JsonParsingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ModelUtilsTest {

    public static final String INSTITUTION_URI = "https://example.org/institution";
    public static final String MODEL_UTILS_DATA = "model_utils_data";
    public static final Path HAS_TYPE_JSON = Path.of(MODEL_UTILS_DATA, "has_type.json");
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
    void addTypeToModelWithUriCreatesInstitutionObject() throws IOException, JsonParsingException {
        ModelUtils modelUtils = new ModelUtils();
        URI uri = URI.create(INSTITUTION_URI);
        modelUtils.addTypeToModel(uri);
        JsonNode resultModel = modelUtils.toJsonLd();
        Object expectedModel = objectify(stringFromResources(HAS_TYPE_JSON));
        assertThat(resultModel, is(equalTo(expectedModel)));
    }

    private JsonNode objectify(String json) throws com.fasterxml.jackson.core.JsonProcessingException {
        return MAPPER.readValue(json, JsonNode.class);
    }

    @DisplayName("Model utils can add name statement for a URI to model")
    @Test
    void addNameStatementWithUriCreatesNameStatement() throws IOException, JsonParsingException {
        ModelUtils modelUtils = new ModelUtils();
        URI uri = URI.create(INSTITUTION_URI);

        // For the frame to work, there must be a node of type Institution
        modelUtils.addNameToModel(uri, SOME_NAME);
        modelUtils.addTypeToModel(uri);
        JsonNode actual = modelUtils.toJsonLd();
        JsonNode expected = objectify(stringFromResources(HAS_NAME_JSON));
        assertThat(actual, is(equalTo(expected)));
    }

    @DisplayName("Model utils can add subunit relation to an object")
    @Test
    void addSubunitsRelationToModelWithUriCreatesSubunitRelation() throws IOException, JsonParsingException {
        ModelUtils modelUtils = new ModelUtils();
        URI institution = URI.create(INSTITUTION_URI);
        URI subunit = URI.create(SUBUNIT_URI);
        modelUtils.addTypeToModel(institution);
        modelUtils.addSubunitsRelationToModel(institution, subunit);
        JsonNode actual = modelUtils.toJsonLd();
        JsonNode expected = objectify(stringFromResources(HAS_SUBUNIT_JSON));
        assertThat(actual, is(equalTo(expected)));
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

    @DisplayName("covertToJsonNode throws JsonParsingException when an exception occurs")
    @Test
    public void convertToJsonNodeThrowsJsonParsingExceptionWhenAnExceptionOccurs()
        throws NoSuchMethodException {
        ModelUtils modelUtils = new ModelUtils();
        Method testedMethod = accessMethod("convertToJsonNode");
        String inputParameter = "Invalid json str";
        Executable action = () -> testedMethod.invoke(modelUtils, inputParameter);
        InvocationTargetException reflectionException = assertThrows(InvocationTargetException.class, action);

        assertThat(reflectionException.getCause().getClass(), is(equalTo(RuntimeException.class)));
        RuntimeException expectedException = (RuntimeException) reflectionException.getCause();
        assertThat(expectedException.getMessage(), containsString(inputParameter));
    }

    public Method accessMethod(String methodName) throws NoSuchMethodException {
        Class<ModelUtils> classAccessor = ModelUtils.class;
        Method testedMethod = classAccessor.getDeclaredMethod(methodName, String.class);
        testedMethod.setAccessible(true);
        return testedMethod;
    }
}