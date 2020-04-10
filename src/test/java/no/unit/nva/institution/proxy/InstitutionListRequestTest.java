package no.unit.nva.institution.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import no.unit.nva.institution.proxy.InstitutionListRequest.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InstitutionListRequestTest {

    public static final String SOME_LANGUAGE = "someLang";
    public static final String SOME_OTHER_LANGUAGE = "someOtherLang";
    private InstitutionListRequest request;

    @BeforeEach
    public void setup() {
        request = new Builder().withLanguage(SOME_LANGUAGE).build();
    }

    @Test
    @DisplayName("Builder sets the language")
    public void builderSetsTheLanguage() {
        InstitutionListRequest request = new Builder().withLanguage(SOME_LANGUAGE).build();
        assertThat(request.getLanguage(), is(equalTo(SOME_LANGUAGE)));
    }

    @Test
    public void getLanguageReturnsTheLanguage() {
        assertThat(request.getLanguage(), is(equalTo(SOME_LANGUAGE)));
    }

    @Test
    public void setLanguageSetsTheLanguage() {
        request.setLanguage(SOME_OTHER_LANGUAGE);
        assertThat(request.getLanguage(), is(equalTo(SOME_OTHER_LANGUAGE)));
    }
}
