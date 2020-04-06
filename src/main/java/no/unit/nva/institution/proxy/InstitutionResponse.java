package no.unit.nva.institution.proxy;

import java.net.URI;
import java.util.Objects;
import nva.commons.utils.JacocoGenerated;

public class InstitutionResponse {
    private URI id;
    private String name;

    private InstitutionResponse(Builder builder) {
        setId(builder.id);
        setName(builder.name);
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @JacocoGenerated
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstitutionResponse)) {
            return false;
        }
        InstitutionResponse that = (InstitutionResponse) o;
        return getId().equals(that.getId())
                && getName().equals(that.getName());
    }

    @Override
    @JacocoGenerated
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    public static final class Builder {
        private URI id;
        private String name;

        public Builder() {
        }

        public Builder withId(URI id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public InstitutionResponse build() {
            return new InstitutionResponse(this);
        }
    }
}
