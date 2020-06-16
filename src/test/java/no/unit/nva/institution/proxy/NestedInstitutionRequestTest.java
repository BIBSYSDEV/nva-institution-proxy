package no.unit.nva.institution.proxy;

import no.unit.nva.institution.proxy.request.NestedInstitutionRequest;
import no.unit.nva.institution.proxy.utils.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;

public class NestedInstitutionRequestTest {

    @DisplayName("NestedInstitutionRequest has a constructor with parameters")
    @Test
    public void nestedInstitutionRequestHasAConstructorWithParameters() {
        NestedInstitutionRequest actual = new NestedInstitutionRequest("SomeUrl", Language.ENGLISH.getCode());
        assertNotNull(actual);
    }
}