package no.unit.nva.institution.proxy.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

class JsonParsingExceptionTest {

    private static final String INVALID_JSON = "invalid json";
    private static final String NESTED_EXCEPTION_MESSAGE = "nested exception message";

    @Test
    void getMessageReturnsStringWithInvalidJsonStringWhenExceptionOccurs() {

        JsonParseException nestedException = mockNestedException();
        JsonParsingException exception = new JsonParsingException(nestedException, INVALID_JSON);
        assertThat(exception.getMessage(), containsString(INVALID_JSON));
    }

    @Test
    void getStatusCodeReturnsInternalSeverError() {
        JsonParseException nestedException = mockNestedException();
        JsonParsingException exception = new JsonParsingException(nestedException, INVALID_JSON);
        assertThat(exception.getStatusCode(), is(equalTo(HttpStatus.SC_INTERNAL_SERVER_ERROR)));
    }

    private JsonParseException mockNestedException() {
        JsonParser parser = mock(JsonParser.class);
        return new JsonParseException(parser, NESTED_EXCEPTION_MESSAGE);
    }
}