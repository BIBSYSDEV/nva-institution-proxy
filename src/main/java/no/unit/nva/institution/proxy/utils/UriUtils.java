package no.unit.nva.institution.proxy.utils;

import java.net.URI;
import java.net.URISyntaxException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import org.apache.http.client.utils.URIBuilder;

public final class UriUtils {

    public static final String QUERY_PARAM_LANGUAGE = "lang";

    private UriUtils() {
    }

    /**
     *  Add the language parameter in the Request URI.
     * @param uri the Institution or Unit URI.
     * @param language the language code ("en", "nb", "nn").
     * @return the URI with the Language parameter
     * @throws InvalidUriException when the input URI is invalid.
     */
    public static URI getUriWithLanguage(URI uri, Language language) throws InvalidUriException {
        try {
            return new URIBuilder(uri)
                .setParameter(QUERY_PARAM_LANGUAGE, language.getCode()).build();
        } catch (URISyntaxException e) {
            throw new InvalidUriException(e.getReason());
        }
    }
}
