package no.unit.nva.institution.proxy;

import no.unit.nva.institution.proxy.utils.Language;

public interface HttpExecutor {

    InstitutionListResponse getInstitutions(Language language) throws Exception;

}
