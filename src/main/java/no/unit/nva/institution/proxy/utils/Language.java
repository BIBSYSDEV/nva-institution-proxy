package no.unit.nva.institution.proxy.utils;

import java.util.Arrays;
import java.util.stream.Collectors;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;

public enum Language {
    ENGLISH("en"),
    NORWEGIAN_BOKMAAL("nb"),
    NORWEGIAN_NYNORSK("nn");

    public static final String DELIMITER = ", ";
    public static final String LANGUAGES_STRING = Arrays.stream(values())
                                                        .map(language -> language.code)
                                                        .collect(Collectors.joining(DELIMITER));
    public static final String UNKNOWN_LANGUAGE_TEMPLATE = "The language \"%s\" is not recognized, use one of %s";

    public static final Language DEFAULT_LANGUAGE = NORWEGIAN_BOKMAAL;

    private String code;

    Language(String code) {
        this.code = code;
    }

    /**
     * Get a Language object by code.
     *
     * @param code The two letter code of the language object
     * @return The corresponding language object
     * @throws UnknownLanguageException In case the code supplied does not match anything
     */
    public static Language getLanguage(String code) throws UnknownLanguageException {
        return Arrays.stream(values())
                     .filter(language -> language.code.equals(code))
                     .findFirst().orElseThrow(() -> new UnknownLanguageException(getFormattedError(code)));
    }

    private static String getFormattedError(String code) {
        return String.format(UNKNOWN_LANGUAGE_TEMPLATE, code, LANGUAGES_STRING);
    }

    public String getCode() {
        return this.code;
    }
}
