package no.unit.nva.institution.proxy;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.utils.HttpExecutorImpl;
import no.unit.nva.institution.proxy.utils.Language;

import static java.util.Objects.isNull;

public class CristinApiClient {

    public static final String LOG_LANGUAGE_MAPPING_TEMPLATE = "Attempting to find language \"%s\"";
    private final HttpExecutor httpExecutor;
    private final LambdaLogger logger;

    protected CristinApiClient(LambdaLogger logger) {
        this.httpExecutor = new HttpExecutorImpl();
        this.logger = logger;
    }

    public CristinApiClient(HttpExecutor httpExecutor, LambdaLogger logger) {
        this.httpExecutor = httpExecutor;
        this.logger = logger;
    }

    public InstitutionListResponse getInstitutions(String languageCode) throws
            Exception {
        return httpExecutor.getInstitutions(getLanguage(languageCode));
    }

    private Language getLanguage(String languageCode) throws UnknownLanguageException {
        if (isNull(languageCode) || languageCode.isBlank()) {
            return Language.NORWEGIAN_BOKMAAL;
        }
        logger.log(String.format(LOG_LANGUAGE_MAPPING_TEMPLATE, languageCode));
        return Language.getLanguage(languageCode);
    }
}