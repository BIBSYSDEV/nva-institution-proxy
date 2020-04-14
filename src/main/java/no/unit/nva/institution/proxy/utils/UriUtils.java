package no.unit.nva.institution.proxy.utils;

import no.unit.nva.institution.proxy.exception.InvalidUriException;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

public class UriUtils {

    public static final String QUERY_PARAM_LANGUAGE = "lang";

    public static URI getUriWithLanguage(URI uri, Language language) throws InvalidUriException {
        try {
            return new URIBuilder(uri)
                    .setParameter(QUERY_PARAM_LANGUAGE, language.getCode()).build();
        } catch (URISyntaxException e) {
            throw new InvalidUriException(e.getReason());
        }
    }
}
