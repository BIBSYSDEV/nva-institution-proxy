package no.unit.nva.institution.proxy.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InstitutionDtoTest {

    public static final String ACRONYM = "acronym";
    public static final String COUNTRY = "country";
    public static final boolean CRISTIN_USER = true;
    public static final String ID = "SomeId";
    public static final Map<String, String> NAME = Collections.singletonMap("key1", "value1");
    public static final URI SOME_URI = URI.create("https://somuri.org");
    private InstitutionDto dto;

    @BeforeEach
    public void setup() {
        this.dto = new InstitutionDto();
        dto.setAcronym(ACRONYM);
        dto.setCountry(COUNTRY);
        dto.setCristinUser(CRISTIN_USER);
        dto.setId(ID);
        dto.setName(NAME);
        dto.setUri(SOME_URI);
    }

    @Test
    @DisplayName("getId returns Id")
    public void getIdReturnsId() {
        assertThat(dto.getId(), is(equalTo(ID)));
    }

    @Test
    @DisplayName("getsName returns name")
    public void getName() {
        assertThat(dto.getName(), is(equalTo(NAME)));
    }

    @Test
    @DisplayName("getAcronym returns acronym")
    public void getAcronym() {
        assertThat(dto.getAcronym(), is(equalTo(ACRONYM)));
    }

    @Test
    @DisplayName("getCounty returns country")
    public void getCountry() {
        assertThat(dto.getCountry(), is(equalTo(COUNTRY)));
    }

    @Test
    @DisplayName("isCristingUser returns whether it is cristin user")
    public void isCristinUser() {
        assertThat(dto.isCristinUser(), is(CRISTIN_USER));
    }

    @Test
    @DisplayName("getUri returns URI")
    public void getUri() {
        assertThat(dto.getUri(), is(SOME_URI));
    }
}