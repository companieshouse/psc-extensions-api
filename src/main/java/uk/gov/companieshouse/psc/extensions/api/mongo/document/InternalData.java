package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalData {
    
    @JsonProperty("internal_id")
    private String internalId;

    public InternalData() {
        // Default constructor for Jackson
    }

    public InternalData(String internalId) {
        this.internalId = internalId;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
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