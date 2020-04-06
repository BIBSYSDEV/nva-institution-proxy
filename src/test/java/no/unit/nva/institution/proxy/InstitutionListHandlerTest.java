package no.unit.nva.institution.proxy;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import nva.commons.utils.Environment;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InstitutionListHandlerTest {

    private static final String SOME_ENV_VALUE = "ANY_VALUE";
    public static final String URI_TEMPLATE = "https://example.org/institution/%d";
    public static final String NAME_TEMPLATE = "SOME_NAME_%s";
    private Environment environment;
    private Context context;
    private LambdaLogger logger;

    /**
     * Setup.
     */
    @BeforeEach
    public void setup() {
        environment = mock(Environment.class);
        when(environment.readEnv(anyString())).thenReturn(SOME_ENV_VALUE);
        context = mock(Context.class);
        when(context.getLogger()).thenReturn(logger);
    }

    @Mock
    CristinApiClient mockCristinApiClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DisplayName("Check that the InstitutionListHandler exists")
    @Test
    void mainHandlerExists() {
        new InstitutionListHandler(environment);
    }

    //    @DisplayName("A well formed request returns a list of institutions")
    //    @Test
    //    void mainHandlerReturnsInstitutionResponseListWhenRequested() throws Exception {
    //        int expectedCount = 5;
    //        when(mockCristinApiClient.getInstitutions(any())).thenReturn(getInstitutionsList(expectedCount));
    //        InstitutionListHandler institutionListHandler = new InstitutionListHandler(environment);
    //        RequestInfo requestInfo = new RequestInfo();
    //        requestInfo.setPath("https://example.org/institutions");
    //        requestInfo.setHeaders(generateHeaders());
    //        InstitutionListResponse response = institutionListHandler.processInput(generateInstitutionListRequest(),
    //                requestInfo, context);
    //        assertNotNull(response);
    //        assertEquals(expectedCount, response.size());
    //    }

    private Map<String, String> generateHeaders() {
        return Collections.singletonMap(HttpHeaders.ACCEPT, APPLICATION_JSON.getMimeType());
    }

    private InstitutionListRequest generateInstitutionListRequest() {
        return new InstitutionListRequest.Builder()
            .withLanguage("")
            .build();
    }

    private InstitutionListResponse getInstitutionsList(int count) {
        return new InstitutionListResponse(IntStream.range(0, count)
            .mapToObj(this::getInstitutionResponse)
            .collect(Collectors.toList()));
    }

    private InstitutionResponse getInstitutionResponse(int counter) {
        return new InstitutionResponse.Builder()
                .withId(URI.create(String.format(URI_TEMPLATE, counter)))
                .withName(String.format(NAME_TEMPLATE, counter))
                .build();
    }
}
