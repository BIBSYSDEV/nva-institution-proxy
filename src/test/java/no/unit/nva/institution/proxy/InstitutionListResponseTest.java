package no.unit.nva.institution.proxy;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstitutionListResponseTest {

    @DisplayName("Constructor can set the institution list")
    @Test
    void institutionListConstructorSetsInstitutionList() {
        List<InstitutionResponse> institutionList = Collections.singletonList(new InstitutionResponse.Builder()
                .withId(URI.create("https:/example.org/institution/1.0.0.0"))
                .withName("Mortenberry").build());
        InstitutionListResponse institutionListResponse = new InstitutionListResponse(institutionList);
        assertEquals(institutionList, institutionListResponse);
    }

}