package no.unit.nva.institution.proxy.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.institution.proxy.InstitutionListResponse;
import no.unit.nva.institution.proxy.InstitutionResponse;
import no.unit.nva.institution.proxy.dto.InstitutionDto;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InstitutionUtils {

    public static final String NO_NAME = "No name";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    protected static InstitutionListResponse toInstitutionListResponse(String institutionsJson) throws IOException {
        List<InstitutionDto> institutions = Arrays.asList(
                OBJECT_MAPPER.readValue(institutionsJson, InstitutionDto[].class));
        return new InstitutionListResponse(institutions.stream().filter(InstitutionDto::isCristinUser)
                .map(InstitutionUtils::toInstitutionResponse)
                .collect(Collectors.toList()));
    }

    protected static InstitutionResponse toInstitutionResponse(InstitutionDto institutionDto) {
        return new InstitutionResponse.Builder()
                .withId(institutionDto.getUri())
                .withName(InstitutionUtils.getAnyName(institutionDto))
                .build();
    }

    private static String getAnyName(InstitutionDto institutionDto) {
        return institutionDto.getName().values().stream().findFirst().orElse(NO_NAME);
    }
}
