package no.unit.nva.institution.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.JsonParsingException;
import no.unit.nva.institution.proxy.exception.NonExistingUnitError;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.utils.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CristinApiClient {

    private final HttpExecutor httpExecutor;
    private static final Logger logger = LoggerFactory.getLogger(CristinApiClient.class);

    public CristinApiClient() {
        this.httpExecutor = new HttpExecutorImpl();
    }

    public CristinApiClient(HttpExecutor httpExecutor) {
        this.httpExecutor = httpExecutor;
    }

    public InstitutionListResponse getInstitutions(Language language) throws GatewayException {
        return httpExecutor.getInstitutions(language);
    }

    /**
     * Get all the descendants of an insitution's logical unit.
     *
     * @param uri      The URI of the institution's Unit
     * @param language a valid Language. See {@link Language}.
     * @return A list of all the units that are descendants of the institution unit
     * @throws UnknownLanguageException when the language is invalid.
     * @throws GatewayException         when an Exception occurs.
     * @throws InvalidUriException      when the input URI is invalid.
     */
    public JsonNode getNestedInstitution(URI uri, Language language)
        throws GatewayException, InvalidUriException, JsonParsingException {
        return httpExecutor.getNestedInstitution(uri, language);
    }

    /**
     * Get a information for a unit (department).
     *
     * @param uri      the Cristin unit URI
     * @param language a language code for the details of each unit
     * @return an {@link JsonNode} containing the information in JSON-LD form
     * @throws InterruptedException when the http client throws an {@link InterruptedException } exception
     * @throws NonExistingUnitError when the URI does not correspond to an existing unit.
     * @throws GatewayException     when Cristin server reports failure
     */
    public JsonNode getSingleUnit(URI uri, Language language)
        throws InterruptedException, NonExistingUnitError, GatewayException {
        logger.info("Fetching resutls for: " + uri.toString());
        JsonNode result = httpExecutor.getSingleUnit(uri, language);
        return result;
    }
}
