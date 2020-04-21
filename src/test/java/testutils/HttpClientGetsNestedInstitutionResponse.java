package testutils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.utils.Language;
import no.unit.nva.institution.proxy.utils.UriUtils;
import nva.commons.utils.IoUtils;
import org.apache.http.HttpStatus;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class HttpClientGetsNestedInstitutionResponse {

    public static final String CRISTIN_RESPONSES_RES_FOLDER = "cristin_responses";
    public static final String MOCK_ERROR = "MockHttp client setup failed for request:";
    public static final String INSTITUTION_PARENT_REQUEST_URI =
        "https://api.cristin.no/v2/units?parent_unit_id=1.0.0.0&per_page=20000";

    public static final String INSTITUTION_REQUEST_URI = "https://api.cristin.no/v2/institutions/1";

    public static final Path EXAMPLE_INSTITUTION = Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_institution.json");

    private static final Path EXAMPLE_INSTITUTION_PARENT =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_parent_institution.json");
    private static final Path CORRESPONDING_UNIT =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_corresponding_unit.json");
    private static final Path ADMINISTRATION =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_administration.json");
    private static final Path ADMINISTRATION_ONE =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_administration_one.json");
    private static final Path ADMINISTRATION_TWO =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_administration_two.json");
    private static final Path CULTURAL_STUDIES =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_cultural_studies.json");
    private static final Path WELSH_LANGUAGE =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_welsh_language.json");
    private static final Path ADMINISTRATION_LH =
        Path.of(CRISTIN_RESPONSES_RES_FOLDER, "nushf_administration_one_lh.json");

    private static final String CORRESPONDING_UNIT_URI = "https://api.cristin.no/v2/units/1.0.0.0";
    private static final String ADMINISTRATION_URI = "https://api.cristin.no/v2/units/1.1.0.0";
    private static final String ADMINISTRATION_ONE_URI = "https://api.cristin.no/v2/units/1.1.1.0";
    private static final String ADMINISTRATION_TWO_URI = "https://api.cristin.no/v2/units/1.1.2.0";
    private static final String ADMINISTRATION_ONE_LH_URI = "https://api.cristin.no/v2/units/1.1.1.1";
    private static final String CULTURAL_STUDIES_URI = "https://api.cristin.no/v2/units/1.3.0.0";
    private static final String WELSH_LANGUAGE_URI = "https://api.cristin.no/v2/units/1.4.0.0";

    private Map<URI, String> requestResponseMapping = new HashMap<>();

    public HttpClientGetsNestedInstitutionResponse(Language language) throws InvalidUriException {
        initializePathResponseMap(language);
    }

    public HttpClient getMockClient() {
        HttpClient client = mock(HttpClient.class);

        when(client.sendAsync(any(HttpRequest.class), any(BodyHandler.class)))
            .thenAnswer(new Answer<CompletableFuture<HttpResponse<String>>>() {
                @Override
                public CompletableFuture<HttpResponse<String>> answer(InvocationOnMock invocation) {
                    HttpRequest request = invocation.getArgument(0);
                    if (requestResponseMapping.containsKey(request.uri())) {
                        HttpResponse<String> response = createMockHttpResponse(request);
                        return CompletableFuture.completedFuture(response);
                    }
                    throw new IllegalStateException(MOCK_ERROR + request.uri().toString());
                }
            });

        return client;
    }

    public HttpResponse<String> createMockHttpResponse(HttpRequest request) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(response.body()).thenReturn(requestResponseMapping.get(request.uri()));
        return response;
    }

    private void initializePathResponseMap(Language language) throws InvalidUriException {
        requestResponseMapping.put(createUri(INSTITUTION_REQUEST_URI, language),
            IoUtils.stringFromResources(EXAMPLE_INSTITUTION));
        requestResponseMapping.put(createUri(INSTITUTION_PARENT_REQUEST_URI, language),
            IoUtils.stringFromResources(EXAMPLE_INSTITUTION_PARENT));
        requestResponseMapping.put(createUri(CORRESPONDING_UNIT_URI, language),
            IoUtils.stringFromResources(CORRESPONDING_UNIT));
        requestResponseMapping.put(createUri(ADMINISTRATION_URI, language),
            IoUtils.stringFromResources(ADMINISTRATION));
        requestResponseMapping.put(createUri(ADMINISTRATION_ONE_URI, language),
            IoUtils.stringFromResources(ADMINISTRATION_ONE));
        requestResponseMapping.put(createUri(ADMINISTRATION_TWO_URI, language),
            IoUtils.stringFromResources(ADMINISTRATION_TWO));
        requestResponseMapping.put(createUri(ADMINISTRATION_ONE_LH_URI, language),
            IoUtils.stringFromResources(ADMINISTRATION_LH));
        requestResponseMapping.put(createUri(CULTURAL_STUDIES_URI, language),
            IoUtils.stringFromResources(CULTURAL_STUDIES));
        requestResponseMapping.put(createUri(WELSH_LANGUAGE_URI, language),
            IoUtils.stringFromResources(WELSH_LANGUAGE));
    }

    private URI createUri(String uri, Language language) throws InvalidUriException {
        return UriUtils.getUriWithLanguage(URI.create(uri), language);
    }
}
