package no.unit.nva.institution.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.unit.nva.institution.proxy.exception.HttpClientFailureException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.NonExistingUnitError;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.response.InstitutionResponse;
import no.unit.nva.institution.proxy.utils.Language;
import nva.commons.utils.IoUtils;
import nva.commons.utils.JsonUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testutils.HttpClientReturningInfoOfSingleUnits;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static testutils.HttpClientReturningInfoOfSingleUnits.SECOND_LEVEL_CHILD_URI;
import static testutils.HttpClientReturningInfoOfSingleUnits.TESTING_LANGUAGE;

public class CristinApiClientTest {

    public static final Language VALID_LANGUAGE_EN = Language.ENGLISH;
    public static final URI VALID_URI = URI.create("https://example.org");
    public static final String SOME_NAME = "SomeName";
    public static final String HTTP_CLIENT_RESPONSES = "httpClientResponses";
    public static final Path SINGLE_UNIT_RESPONSE_GRAPH = Path.of(HTTP_CLIENT_RESPONSES,
        "singleUnitResponseGraph.json");
    public static final String SOME_VALUE = "some_value";
    public static final String SOME_KEY = "some_key";
    public static final String SOME_ACRONYM = "SomeAcronym";

    @DisplayName("getNestedInstitution returns nested institution when input is valid")
    @Test
    void getNestedInstitutionReturnsNestedInstitutionWhenInputIsValid()
        throws InvalidUriException, HttpClientFailureException {
        HttpExecutor mockHttpExecutor = mock(HttpExecutorImpl.class);
        ObjectNode mockResponse = simpleJsonObject();
        when(mockHttpExecutor.getNestedInstitution(any(), any()))
            .thenReturn(mockResponse);
        CristinApiClient cristinApiClient = new CristinApiClient(mockHttpExecutor);
        JsonNode response = cristinApiClient.getNestedInstitution(VALID_URI, VALID_LANGUAGE_EN);
        assertThat(response, is(equalTo(mockResponse)));
    }

    @DisplayName("getInstitutions returns a list with Institutions when input is valid")
    @Test
    void getInstitutionsReturnsAListWithInstitutionsWhenInputIsValid() throws HttpClientFailureException {
        InstitutionResponse mockResponseItem = new InstitutionResponse.Builder()
            .withId(VALID_URI)
            .withName(SOME_NAME)
            .withAcronym(SOME_ACRONYM)
            .build();
        HttpExecutor mockHttpExecutor = mock(HttpExecutorImpl.class);
        when(mockHttpExecutor.getInstitutions(any(Language.class)))
            .thenReturn(new InstitutionListResponse(Collections.singletonList(mockResponseItem)));
        CristinApiClient cristinApiClient = new CristinApiClient(mockHttpExecutor);
        InstitutionListResponse response = cristinApiClient.getInstitutions(VALID_LANGUAGE_EN);
        assertNotNull(response);
    }

    @DisplayName("getInstitutions returns a list with Institutions and each institution has an acronym")
    @Test
    void getInstitutionsReturnsInstitutionsWithAcronymIfInstitutionsHaveAcronyms() throws HttpClientFailureException {
        InstitutionResponse mockResponseItem = new InstitutionResponse.Builder()
            .withId(VALID_URI)
            .withName(SOME_NAME)
            .withAcronym(SOME_ACRONYM)
            .build();
        HttpExecutor mockHttpExecutor = mock(HttpExecutorImpl.class);
        when(mockHttpExecutor.getInstitutions(any(Language.class)))
            .thenReturn(new InstitutionListResponse(Collections.singletonList(mockResponseItem)));
        CristinApiClient cristinApiClient = new CristinApiClient(mockHttpExecutor);
        InstitutionListResponse response = cristinApiClient.getInstitutions(VALID_LANGUAGE_EN);
        assertThat(response.size(), is(equalTo(1)));
        assertThat(response.get(0).getAcronym(), is(not(nullValue())));
    }

    @Test
    @DisplayName("getSingleUnit returns the graph of a unit")
    public void getSingleUnitReturnsTheGraphOfAUnit()
        throws InterruptedException, HttpClientFailureException, NonExistingUnitError, JsonProcessingException {
        HttpExecutorImpl httpExecutor = new HttpExecutorImpl(new HttpClientReturningInfoOfSingleUnits());
        CristinApiClient cristinApiClient = new CristinApiClient(httpExecutor);

        JsonNode actualResponse = cristinApiClient.getSingleUnit(SECOND_LEVEL_CHILD_URI, TESTING_LANGUAGE);
        JsonNode expectedResponse = JsonUtils.objectMapper.readTree(IoUtils.stringFromResources(
            CristinApiClientTest.SINGLE_UNIT_RESPONSE_GRAPH));
        assertThat(actualResponse, is(equalTo(expectedResponse)));
    }

    private ObjectNode simpleJsonObject() {
        ObjectNode mockResponse = JsonUtils.objectMapper.createObjectNode();
        mockResponse.put(SOME_KEY, SOME_VALUE);
        return mockResponse;
    }
}