package no.unit.nva.institution.proxy.handler;

import com.amazonaws.services.lambda.runtime.Context;
import no.unit.nva.institution.proxy.CristinApiClient;
import no.unit.nva.institution.proxy.exception.HttpClientFailureException;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.utils.LanguageMapper;
import nva.commons.handlers.ApiGatewayHandler;
import nva.commons.handlers.RequestInfo;
import nva.commons.utils.Environment;
import nva.commons.utils.JacocoGenerated;
import org.apache.http.HttpStatus;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;

public class InstitutionListHandler extends ApiGatewayHandler<Void, InstitutionListResponse> {

    public static final String LANGUAGE_QUERY_PARAMETER = "language";
    private final CristinApiClient cristinApiClient;
    private InstitutionListResponse response;

    @JacocoGenerated
    public InstitutionListHandler() {
        this(new Environment(), new CristinApiClient());
    }

    /**
     * In testing, it is necessary to pass the environment to the constructor.
     */
    public InstitutionListHandler(Environment environment, CristinApiClient cristinApiClient) {
        super(Void.class, environment, LoggerFactory.getLogger(InstitutionListHandler.class));
        this.cristinApiClient = cristinApiClient;
    }

    @Override
    protected InstitutionListResponse processInput(Void input, RequestInfo requestInfo,
                                                   Context context)
        throws UnknownLanguageException, HttpClientFailureException {
        String languageParameter = requestInfo.getQueryParameters().get(LANGUAGE_QUERY_PARAMETER);
        LanguageMapper languageMapper = new LanguageMapper();
        if (isNull(response)) {
            response = cristinApiClient.getInstitutions(languageMapper.getLanguage(languageParameter));
        }
        return response;
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, InstitutionListResponse output) {
        return HttpStatus.SC_OK;
    }
}