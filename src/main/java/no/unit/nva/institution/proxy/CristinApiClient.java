package no.unit.nva.institution.proxy;

import static java.util.Objects.isNull;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.net.URI;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.response.NestedInstitutionResponse;
import no.unit.nva.institution.proxy.utils.Language;

public class CristinApiClient {

    public static final String LOG_LANGUAGE_MAPPING_TEMPLATE = "Attempting to find language \"%s\"";
    public static final String LOG_URI_ERROR_TEMPLATE = "The supplied URI <%s> was invalid";
    private final HttpExecutor httpExecutor;
    private final LambdaLogger logger;

    public CristinApiClient(LambdaLogger logger) {
        this.httpExecutor = new HttpExecutorImpl();
        this.logger = logger;
    }

    public CristinApiClient(HttpExecutor httpExecutor, LambdaLogger logger) {
        this.httpExecutor = httpExecutor;
        this.logger = logger;
    }

    /**
     *  Get all Institutions.
     * @param languageCode a language code.
     * @return A list of institutions
     * @throws UnknownLanguageException when the language code is invalid.
     * @throws GatewayException when another exception occurs.
     */
    public InstitutionListResponse getInstitutions(String languageCode)
        throws UnknownLanguageException, GatewayException {
        return httpExecutor.getInstitutions(getLanguage(languageCode));
    }

    /**
     * Get all the descendedants of an insitution's logical unit.
     *
     * @param uri          The URI of the institution's Unit
     * @param languageCode a valid language code. See {@link Language}.
     * @return A list of all the units that are descendants of the institution unit
     * @throws UnknownLanguageException when the language is invalid.
     * @throws GatewayException         when an Exception occurs.
     * @throws InvalidUriException      when the input URI is invalid.
     */
    public NestedInstitutionResponse getNestedInstitution(String uri, String languageCode)
        throws UnknownLanguageException, GatewayException, InvalidUriException {
        URI parsedUri;
        try {
            parsedUri = URI.create(uri);
        } catch (Exception e) {
            logger.log(String.format(LOG_URI_ERROR_TEMPLATE, uri));
            throw new InvalidUriException(uri);
        }
        return httpExecutor.getNestedInstitution(parsedUri, getLanguage(languageCode));
    }

    private Language getLanguage(String languageCode) throws UnknownLanguageException {
        if (isNull(languageCode) || languageCode.isBlank()) {
            return Language.NORWEGIAN_BOKMAAL;
        }
        logger.log(String.format(LOG_LANGUAGE_MAPPING_TEMPLATE, languageCode));
        return Language.getLanguage(languageCode);
    }
}
