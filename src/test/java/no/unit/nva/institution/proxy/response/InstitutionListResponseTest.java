package no.unit.nva.institution.proxy.response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InstitutionListResponseTest {

    @DisplayName("Default constructor builds an empty list")
    @Test
    public void defaultConstructorBuildsAnEmptyList() {
        InstitutionListResponse list = new InstitutionListResponse();
        assertThat(list, is(empty()));
    }

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