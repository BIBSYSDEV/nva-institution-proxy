package no.unit.nva.institution.proxy;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.NonExistingUnitError;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.response.NestedInstitutionResponse;
import no.unit.nva.institution.proxy.utils.Language;

import java.net.URI;
import java.util.concurrent.ExecutionException;

public class CristinApiClient {

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
    public NestedInstitutionResponse getNestedInstitution(URI uri, Language language) throws GatewayException,
            InvalidUriException {
        return httpExecutor.getNestedInstitution(uri, language);
    }


    public NestedInstitutionResponse getSingleUnit(URI uri, Language language) throws InterruptedException,
            ExecutionException, InvalidUriException, NonExistingUnitError {
        return httpExecutor.getSingleUnit(uri, language);
    }
}
