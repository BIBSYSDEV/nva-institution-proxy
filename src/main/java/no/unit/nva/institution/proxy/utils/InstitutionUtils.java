package no.unit.nva.institution.proxy.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import no.unit.nva.institution.proxy.InstitutionListResponse;
import no.unit.nva.institution.proxy.InstitutionResponse;
import no.unit.nva.institution.proxy.dto.InstitutionDto;
import nva.commons.utils.JsonUtils;

public final class InstitutionUtils {

    public static final String NO_NAME = "No name";
    public static final String PARSE_ERROR = "Failed to parse: ";

    private InstitutionUtils() {
    }

    /**
     * Map external (Cristin) institutions to the internal model.
     *
     * @param institutionsJson jsonString as received from the external provider
     * @return a list of institutions
     * @throws IOException when the parsing of the JSON string fails.
     */
    public static InstitutionListResponse toInstitutionListResponse(String institutionsJson)
        throws IOException {
        try {
            List<InstitutionDto> institutions = Arrays.asList(
                JsonUtils.jsonParser.readValue(institutionsJson, InstitutionDto[].class));
            return new InstitutionListResponse(institutions
                .stream()
                .filter(InstitutionDto::isCristinUser)
                .map(InstitutionUtils::toInstitutionResponse)
                .collect(Collectors.toList()));
        } catch (IOException e) {
            throw new IOException(PARSE_ERROR + institutionsJson, e);
        }
    }

    private static InstitutionResponse toInstitutionResponse(InstitutionDto institutionDto) {
        return new InstitutionResponse.Builder()
            .withId(institutionDto.getUri())
            .withName(InstitutionUtils.getAnyName(institutionDto))
            .build();
    }

    private static String getAnyName(InstitutionDto institutionDto) {
        return institutionDto.getName().values().stream().findFirst().orElse(NO_NAME);
    }
}
