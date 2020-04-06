package no.unit.nva.institution.proxy;

import no.unit.nva.institution.proxy.utils.Language;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockHttpExecutor implements HttpExecutor {
    @Override
    public InstitutionListResponse getInstitutions(Language language) throws Exception {
        return generatedInstitutionListResponse();
    }

    private InstitutionListResponse generatedInstitutionListResponse() {
        List<InstitutionResponse> institutions = IntStream.rangeClosed(0, 5)
                .mapToObj(this::generateInstitutionResponse).collect(Collectors.toList());
        return new InstitutionListResponse(institutions);
    }

    private InstitutionResponse generateInstitutionResponse(int i) {
        return new InstitutionResponse.Builder()
                .withId(URI.create(String.format("https://example.org/institution/%d", i)))
                .withName(String.format("FAKE_NAME_%d", i))
                .build();
    }
}