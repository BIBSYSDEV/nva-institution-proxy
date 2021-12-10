package no.unit.nva.institution.proxy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.institution.proxy.CristinApiClient;
import no.unit.nva.institution.proxy.exception.HttpClientFailureException;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.utils.LanguageMapper;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.JsonUtils;
import org.apache.http.HttpStatus;

public class InstitutionListHandler extends ApiGatewayHandler<Void, InstitutionListResponse> {

    public static final String LANGUAGE_QUERY_PARAMETER = "language";
    private final CristinApiClient cristinApiClient;
    private static final ObjectMapper objectMapper = JsonUtils.dtoObjectMapper;

    @JacocoGenerated
    public InstitutionListHandler() {
        this(new Environment(), new CristinApiClient());
    }

    /**
     * In testing, it is necessary to pass the environment to the constructor.
     */
    public InstitutionListHandler(Environment environment, CristinApiClient cristinApiClient) {
        super(Void.class, environment, objectMapper);
        this.cristinApiClient = cristinApiClient;
    }

    @Override
    protected InstitutionListResponse processInput(Void input, RequestInfo requestInfo,
                                                   Context context)
        throws UnknownLanguageException, HttpClientFailureException {
        String languageParameter = requestInfo.getQueryParameters().get(LANGUAGE_QUERY_PARAMETER);
        LanguageMapper languageMapper = new LanguageMapper();
        return cristinApiClient.getInstitutions(languageMapper.getLanguage(languageParameter));
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, InstitutionListResponse output) {
        return HttpStatus.SC_OK;
    }
}