package no.unit.nva.institution.proxy.utils;

import java.util.Map;

public class MapUtils {
    public static final String NO_NAME = "NO NAME";

    public static String getNameValue(Map<String,  String> map) {
        return map.entrySet().stream().findAny().map(Map.Entry::getValue).orElse(NO_NAME);
    }
}
