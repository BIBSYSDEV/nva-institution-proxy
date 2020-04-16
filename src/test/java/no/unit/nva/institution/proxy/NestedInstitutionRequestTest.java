package no.unit.nva.institution.proxy;

import static org.junit.Assert.assertNotNull;

import no.unit.nva.institution.proxy.utils.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NestedInstitutionRequestTest {

    @DisplayName("NestedInstitutionRequest has a constuctor with parameters")
    @Test
    public void nestedInstitutionRequestHasAConstuctorWithParameters() {
        NestedInstitutionRequest actual = new NestedInstitutionRequest("SomeUrl", Language.ENGLISH.getCode());
        assertNotNull(actual);
    }
}