package no.unit.nva.institution.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static testutils.HttpClientReturningInfoOfSingleUnits.SECOND_LEVEL_CHILD_URI;
import static testutils.HttpClientReturningInfoOfSingleUnits.TESTING_LANGUAGE;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.NonExistingUnitError;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.response.InstitutionResponse;
import no.unit.nva.institution.proxy.response.NestedInstitutionResponse;
import no.unit.nva.institution.proxy.utils.Language;
import nva.commons.utils.IoUtils;
import nva.commons.utils.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testutils.HttpClientReturningInfoOfSingleUnits;

public class CristinApiClientTest {

    public static final Language VALID_LANGUAGE_EN = Language.ENGLISH;
    public static final URI VALID_URI = URI.create("https://example.org");
    public static final String JSON_VALUE = "true";
    public static final String SOME_NAME = "SomeName";
    public static final String HTTP_CLIENT_RESPONSES = "httpClientResponses";
    public static final Path SINGLE_UNIT_RESPONSE_GRAPH = Path.of(HTTP_CLIENT_RESPONSES,
        "singleUnitResponseGraph.json");

    @DisplayName("getNestedInstitution returns nested institution when input is valid")
    @Test
    void getNestedInstitutionReturnsNestedInstitutionWhenInputIsValid()
        throws InvalidUriException, GatewayException {
        HttpExecutor mockHttpExecutor = mock(HttpExecutorImpl.class);
        when(mockHttpExecutor.getNestedInstitution(any(), any()))
            .thenReturn(new NestedInstitutionResponse(JSON_VALUE));
        CristinApiClient cristinApiClient = new CristinApiClient(mockHttpExecutor);
        NestedInstitutionResponse response = cristinApiClient.getNestedInstitution(VALID_URI, VALID_LANGUAGE_EN);
        assertThat(response.getJson(), containsString(JSON_VALUE));
    }

    @DisplayName("getInstitutions returns a list with Institutions when input is valid")
    @Test
    void getInstitutionsReturnsAListWithInstitutionsWhenInputIsBalid() throws GatewayException {
        InstitutionResponse mockResponseItem = new InstitutionResponse.Builder()
            .withId(VALID_URI)
            .withName(SOME_NAME)
            .build();
        HttpExecutor mockHttpExecutor = mock(HttpExecutorImpl.class);
        when(mockHttpExecutor.getInstitutions(any(Language.class)))
            .thenReturn(new InstitutionListResponse(Collections.singletonList(mockResponseItem)));
        CristinApiClient cristinApiClient = new CristinApiClient(mockHttpExecutor);
        InstitutionListResponse response = cristinApiClient.getInstitutions(VALID_LANGUAGE_EN);
        assertNotNull(response);
    }

    @Test
    @DisplayName("getSingleUNit returns the graph of a unit")
    public void getSingleUnitReturnsTheGraphOfAUnit()
        throws InterruptedException, ExecutionException, GatewayException, InvalidUriException, NonExistingUnitError {
        HttpExecutorImpl httpExecutor = new HttpExecutorImpl(new HttpClientReturningInfoOfSingleUnits());
        CristinApiClient cristinApiClient = new CristinApiClient(httpExecutor);
        NestedInstitutionResponse response = cristinApiClient.getSingleUnit(SECOND_LEVEL_CHILD_URI, TESTING_LANGUAGE);
        String actualResponse = StringUtils.removeWhiteSpaces(response.getJson());
        String expectedResponse = StringUtils.removeWhiteSpaces(IoUtils.stringFromResources(
            CristinApiClientTest.SINGLE_UNIT_RESPONSE_GRAPH));
        assertThat(actualResponse, is(equalTo(expectedResponse)));
    }
}