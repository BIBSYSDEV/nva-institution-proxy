package no.unit.nva.institution.proxy;

import static nva.commons.utils.attempt.Try.attempt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collections;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.response.NestedInstitutionResponse;
import no.unit.nva.institution.proxy.utils.Language;
import no.unit.nva.institution.proxy.utils.LanguageMapper;
import no.unit.nva.testutils.TestLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CristinApiClientTest {

    public static final Language VALID_LANGUAGE_EN = Language.ENGLISH;
    public static final URI VALID_URI = URI.create("https://example.org");
    public static final String JSON_VALUE = "true";
    TestLogger testLogger = new TestLogger();


    @DisplayName("getNestedInstitution returns nested institution when input is valid")
    @Test
    void getNestedInstitutionReturnsNestedInstitutionWhenInputIsValid()
        throws InvalidUriException, GatewayException {
        HttpExecutor mockHttpExecutor = mock(HttpExecutorImpl.class);
        when(mockHttpExecutor.getNestedInstitution(any(), any()))
            .thenReturn(new NestedInstitutionResponse(JSON_VALUE));
        CristinApiClient cristinApiClient = new CristinApiClient(mockHttpExecutor, testLogger);
        NestedInstitutionResponse response = cristinApiClient.getNestedInstitution(VALID_URI, VALID_LANGUAGE_EN);
        assertThat(response.getJson(), containsString(JSON_VALUE));
    }

}