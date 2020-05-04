package no.unit.nva.institution.proxy.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import no.unit.nva.institution.proxy.dto.InstitutionDto;
import no.unit.nva.institution.proxy.response.InstitutionListResponse;
import no.unit.nva.institution.proxy.response.InstitutionResponse;
import nva.commons.utils.IoUtils;
import nva.commons.utils.JsonUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InstitutionUtilsTest {

    public static final String CRISTIN_RESOURCES = "cristin_responses";
    public static final Path CRISTIN_RESPONSE = Path.of(CRISTIN_RESOURCES, "all_institutions_nb.json");
    public static final String EMPTY_ARRAY = "[]";
    private static final String INVALID_JSON = "{noQuotes:here}";

    @DisplayName("toInstitutionListResponse returns an InstitutionListResponse for a valid institutionDto JSON array")
    @Test
    public void toInstitutionListResponseReturnsAnInstitutionListResponseForAvalidInstitutionDtoJsonArray()
        throws IOException {
        String input = IoUtils.stringFromResources(CRISTIN_RESPONSE);
        URI[] expectedArray = urisFromInstitutionsList(input);
        InstitutionListResponse actual = InstitutionUtils.toInstitutionListResponse(input);
        List<URI> actualIds = actual.stream().map(InstitutionResponse::getId).collect(Collectors.toList());
        assertThat(actualIds, containsInAnyOrder(expectedArray));
    }

    @DisplayName("toInstitutionListResponse returns an empty list when input is an empty array")
    @Test
    public void toInstitutionListResponseReturnsAnEmptyListWhenInputIsAnEmptyArray()
        throws IOException {
        InstitutionListResponse actual = InstitutionUtils.toInstitutionListResponse(EMPTY_ARRAY);
        List<URI> actualIds = actual.stream().map(InstitutionResponse::getId).collect(Collectors.toList());
        assertThat(actualIds, is(empty()));
    }

    @DisplayName("toInstitutionListResponse throws an IOException when parsing fails")
    @Test
    public void toInstitutionListResponseThrowsAnIoExceptionWhenParsingFails() {
        assertThrows(IOException.class, () -> InstitutionUtils.toInstitutionListResponse(INVALID_JSON));
    }

    @DisplayName("toInstitutionListResponse returns the invalid JSON string in the Exception message")
    @Test
    public void toInstitutionListResponseReturnsTheInvalidJsonStringInTheExceptionMessage() {
        IOException exception = assertThrows(IOException.class,
            () -> InstitutionUtils.toInstitutionListResponse(INVALID_JSON));
        assertThat(exception.getMessage(), containsString(INVALID_JSON));
    }

    private URI[] urisFromInstitutionsList(String input) throws IOException {
        InstitutionDto[] institutions = JsonUtils.objectMapper.readValue(input, InstitutionDto[].class);
        List<URI> expectedIds = Arrays.stream(institutions)
            .filter(InstitutionDto::isCristinUser)
            .map(InstitutionDto::getUri)
            .collect(Collectors.toList());
        URI[] expectedArray = new URI[expectedIds.size()];
        expectedIds.toArray(expectedArray);
        return expectedArray;
    }
}