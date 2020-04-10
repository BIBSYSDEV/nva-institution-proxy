package no.unit.nva.institution.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import no.unit.nva.institution.proxy.exception.FailedHttpRequestException;
import no.unit.nva.institution.proxy.exception.InstitutionFailureException;
import no.unit.nva.institution.proxy.utils.InstitutionUtils;
import no.unit.nva.institution.proxy.utils.Language;
import nva.commons.utils.IoUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpExecutorImplTest {

    public static final String CRISTIN_RESPONSES_RES_FOLDER = "cristin_responses";
    public static final Path SINGLE_CRISTIN_USER_INSTITUTION = Path.of(CRISTIN_RESPONSES_RES_FOLDER,
        "one_institution_nb.json");
    public static final Path MANY_CRISTIN_USER_INSTITUTIONS = Path.of(CRISTIN_RESPONSES_RES_FOLDER,
        "all_institutions_nb.json");
    private static final String EMPTY_ARRAY = "[]";
    private static final String INVALID_JSON_STR = "Invalid json object";
    private static final String HTTP_ERROR_RESPONSE = "Http response error";

    /**
     * Setup tests.
     */
    @DisplayName("getInstitutions returns an InstitutionListResponse with one object when cristin response"
        + "contains one object ")
    @Test
    public void getInstitutionReturnsAnInstitutionListResponseWithOneObjectWhenCristinResponseContainsOneObject()
        throws Exception {
        String jsonBody = IoUtils.stringFromResources(SINGLE_CRISTIN_USER_INSTITUTION);
        HttpClient client = httpClientWithResponseBody(jsonBody);
        InstitutionListResponse expectedInstitution = InstitutionUtils.toInstitutionListResponse(jsonBody);

        HttpExecutorImpl executor = new HttpExecutorImpl(client);

        InstitutionListResponse actual = executor.getInstitutions(Language.ENGLISH);
        assertThat(actual, is(not(nullValue())));
        assertThat(actual.size(), is(greaterThan(0)));
        assertThat(actual, is(equalTo(expectedInstitution)));
    }

    @DisplayName("getInstitutions returns an InstitutionListResponse with many objects when cristin response"
        + "contains one object ")
    @Test
    public void getInstitutionReturnsAnInstitutionListResponseWithManyObjectsWhenCristinResponseContainsOneObject()
        throws Exception {
        String jsonBody = IoUtils.stringFromResources(MANY_CRISTIN_USER_INSTITUTIONS);
        HttpClient client = httpClientWithResponseBody(jsonBody);
        InstitutionListResponse expectedInstitution = InstitutionUtils.toInstitutionListResponse(jsonBody);

        HttpExecutorImpl executor = new HttpExecutorImpl(client);

        InstitutionListResponse actual = executor.getInstitutions(Language.ENGLISH);
        assertThat(actual, is(not(nullValue())));
        assertThat(actual.size(), is(greaterThan(1)));
        assertThat(actual, is(equalTo(expectedInstitution)));
    }

    @DisplayName("getInstitutions returns an empty list when cristin response contains no objects ")
    @Test
    public void getInstitutionReturnsAnEmptyListWhenCristinResponseContainsNoObjects()
        throws Exception {
        HttpClient client = httpClientWithResponseBody(EMPTY_ARRAY);
        InstitutionListResponse expectedInstitution = new InstitutionListResponse(Collections.emptyList());
        HttpExecutorImpl executor = new HttpExecutorImpl(client);
        InstitutionListResponse actual = executor.getInstitutions(Language.ENGLISH);
        assertThat(actual, is(not(nullValue())));
        assertThat(actual.size(), is(equalTo(0)));
        assertThat(actual, is(equalTo(expectedInstitution)));
    }

    @DisplayName("getInstitutions throws an InstitutionFailureException when client returns an error status code")
    @Test
    public void getInstitutionsThrowsAnInstitutionFailureExceptionWhenClientThrowsException() {

        HttpExecutorImpl executor = new HttpExecutorImpl(httpClientReturnsError());

        InstitutionFailureException exception = assertThrows(InstitutionFailureException.class,
            () -> executor.getInstitutions(Language.ENGLISH));

        Throwable cause = exception.getCause();
        assertThat(cause.getClass(), is(equalTo(FailedHttpRequestException.class)));
        assertThat(exception.getStatusCode(), is(equalTo(FailedHttpRequestException.ERROR_CODE)));
        assertThat(exception.getMessage(), containsString(HTTP_ERROR_RESPONSE));
    }

    @DisplayName("getInstitutions throws an InstitutionFailureException when parsing of response body fails")
    @Test
    public void getInstitutionsThrowsAnInstitutionFailureExceptionWhenParsingOfResponseFails() {

        HttpExecutorImpl executor = new HttpExecutorImpl(httpClientWithResponseBody(INVALID_JSON_STR));

        InstitutionFailureException exception = assertThrows(InstitutionFailureException.class,
            () -> executor.getInstitutions(Language.ENGLISH));

        Throwable cause = exception.getCause();
        assertThat(cause.getClass(), is(equalTo(IOException.class)));
        assertThat(exception.getStatusCode(), is(equalTo(InstitutionFailureException.ERROR_CODE)));
        assertThat(exception.getMessage(), containsString(INVALID_JSON_STR));
    }

    private HttpClient httpClientReturnsError() {
        HttpClient client = mock(HttpClient.class);
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
        when(response.body()).thenReturn(HTTP_ERROR_RESPONSE);
        when(client.sendAsync(any(HttpRequest.class), any(BodyHandler.class)))
            .thenReturn(CompletableFuture.completedFuture(response));
        return client;
    }

    private HttpClient httpClientWithResponseBody(String responseBody) {
        HttpClient client = mock(HttpClient.class);
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(client.sendAsync(any(HttpRequest.class), any(BodyHandler.class)))
            .thenReturn(CompletableFuture.completedFuture(httpResponse));
        return client;
    }
}
