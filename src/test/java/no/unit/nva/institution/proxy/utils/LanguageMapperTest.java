package no.unit.nva.institution.proxy.utils;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;
import nva.commons.utils.log.LogUtils;
import nva.commons.utils.log.TestAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LanguageMapperTest {

    public static final String BLANK_STRING = "  ";
    private static final String INVALID_LANGUAGE_STRING = "invalidLang";

    private LanguageMapper languageMapper;

    /**
     * Setup.
     */
    @BeforeEach
    public void setUp() {
        languageMapper = new LanguageMapper();
    }

    @Test
    public void languageMapperReturnsLanguageForValidLanguageEntry() {
        Arrays.stream(Language.values())
              .forEach(this::assertThatLanguageCodeIsRecognized);
    }

    @Test
    public void languageMapperReturnsDefaultLanguageWhenForNullInput() throws UnknownLanguageException {
        Language actual = languageMapper.getLanguage(null);
        assertThat(actual, is(equalTo(Language.DEFAULT_LANGUAGE)));
    }

    @Test
    public void languageMapperReturnsDefaultLanguageWhenForBlankInput() throws UnknownLanguageException {
        Language actual = languageMapper.getLanguage(BLANK_STRING);
        assertThat(actual, is(equalTo(Language.DEFAULT_LANGUAGE)));
    }

    @Test
    public void languageMapperLogsLanguageMappingEffortWhenInputIsValid() {
        String inputCode = Language.ENGLISH.getCode();
        assertThatMapperLogsInputString(inputCode);
    }

    @Test
    public void languageMapperLogsLanguageMappingEffortWhenInputIsInValid() {
        String inputCode = INVALID_LANGUAGE_STRING;
        assertThatMapperLogsInputString(inputCode);
    }

    public void assertThatMapperLogsInputString(String inputCode) {
        TestAppender appender = LogUtils.getTestingAppender(LanguageMapper.class);
        try {
            languageMapper.getLanguage(inputCode);
        } catch (UnknownLanguageException e) {
            // do nothing
        }
        String logs = appender.getMessages();
        assertThat(logs, containsString(inputCode));
    }

    private void assertThatLanguageCodeIsRecognized(Language expected) {
        Language actual = null;
        try {
            actual = languageMapper.getLanguage(expected.getCode());
            assertThat(actual, is(equalTo(expected)));
        } catch (UnknownLanguageException e) {
            throw new IllegalStateException(e);
        }
    }
}