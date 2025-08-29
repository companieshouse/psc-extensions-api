package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class InternalData {
    
    @JsonProperty("internal_id")
    private String internalId;

    public InternalData() {
        // Default constructor for Jackson
    }

    public InternalData(String internalId) {
        this.internalId = internalId;
    }

    public InternalData(InternalData other) {
        this.internalId = other.internalId;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalData that = (InternalData) o;
        return Objects.equals(internalId, that.internalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String internalId;

        public Builder internalId(String internalId) {
            this.internalId = internalId;
            return this;
        }

        public InternalData build() {
            return new InternalData(internalId);
        }
    }
}