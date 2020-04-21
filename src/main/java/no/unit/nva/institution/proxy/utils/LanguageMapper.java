package no.unit.nva.institution.proxy.utils;

import static java.util.Objects.isNull;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import no.unit.nva.institution.proxy.exception.UnknownLanguageException;

public class LanguageMapper {

    public static final String LOG_LANGUAGE_MAPPING_TEMPLATE = "Attempting to find language \"%s\"";

    private final LambdaLogger logger;

    public LanguageMapper(LambdaLogger logger) {
        this.logger = logger;
    }

    public Language getLanguage(String languageCode) throws UnknownLanguageException {
        if (isNull(languageCode) || languageCode.isBlank()) {
            return Language.DEFAULT_LANGUAGE;
        }
        logger.log(String.format(LOG_LANGUAGE_MAPPING_TEMPLATE, languageCode));
        return Language.getLanguage(languageCode);
    }
}
