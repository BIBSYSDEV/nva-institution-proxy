package no.unit.nva.institution.proxy.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;

import java.net.URI;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UriUtilsTest {

    private static final URI TEST_URI = URI.create("http://hello-world.com");
    private static final Language TESTING_LANGUAGE = Language.ENGLISH;
    private static final String EXPECTED_PARAMETER =
        String.format("%s=%s", UriUtils.QUERY_PARAM_LANGUAGE, TESTING_LANGUAGE.getCode());

    @Test
    @DisplayName("getUriWithLanguage returns URI with language tag when input is URI without language tag")
    public void getUriWithLanguageReturnsUriWithLanguageTagWhenInputIsUriNotContainingLanguageTag()
        throws InvalidUriException {
        URI actual = UriUtils.getUriWithLanguage(TEST_URI, TESTING_LANGUAGE);
        assertThat(actual.toString(), containsString(EXPECTED_PARAMETER));
    }

    @Test
    @DisplayName("getUriWithLanguage returns URI with the new language tag when input is URI with another language tag")
    public void getUriWithLanguageReturnsUriWithNewLanguageTagWhenInputIsUriContainingOtherLanguageTag()
        throws InvalidUriException {
        assertThat(Language.NORWEGIAN_BOKMAAL, is(not(equalTo(TESTING_LANGUAGE))));

        URI input = UriUtils.getUriWithLanguage(TEST_URI, Language.NORWEGIAN_BOKMAAL);
        URI actual = UriUtils.getUriWithLanguage(input, TESTING_LANGUAGE);
        assertThat(actual.toString(), containsString(EXPECTED_PARAMETER));
    }

    @Test
    @DisplayName("clearParameters removes language-tag from URI")
    public void clearParametersRemovesLanguageTagFromUri() throws InvalidUriException {
        URI input = UriUtils.getUriWithLanguage(TEST_URI, TESTING_LANGUAGE);
        URI actual = UriUtils.clearParameters(input);
        assertThat(actual, (is(not(equalTo(input)))));
        assertThat(actual, (is(equalTo(TEST_URI))));
    }
}