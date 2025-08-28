package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class ExtensionDetails {

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String extensionReason;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String extensionStatus;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private LocalDate extensionRequestDate;

        public ExtensionDetails() {
                // Default constructor
        }

        public String getExtensionReason() {
                return extensionReason;
        }

        public void setExtensionReason(String extensionReason) {
                this.extensionReason = extensionReason;
        }

        public String getExtensionStatus() {
                return extensionStatus;
        }

        public void setExtensionStatus(String extensionStatus) {
                this.extensionStatus = extensionStatus;
        }

        public LocalDate getExtensionRequestDate() {
                return extensionRequestDate;
        }

        public void setExtensionRequestDate(LocalDate extensionRequestDate) {
                this.extensionRequestDate = extensionRequestDate;
        }

        private ExtensionDetails(Builder builder) {
                this.extensionReason = builder.extensionReason;
                this.extensionStatus = builder.extensionStatus;
                this.extensionRequestDate = builder.extensionRequestDate;
        }

        public static Builder newBuilder() {
                return new Builder();
        }

        public static class Builder {
                private String extensionReason;
                private String extensionStatus;
                private LocalDate extensionRequestDate;

                public Builder extensionReason(String extensionReason) {
                        this.extensionReason = extensionReason;
                        return this;
                }

                public Builder extensionStatus(String extensionStatus) {
                        this.extensionStatus = extensionStatus;
                        return this;
                }

                public Builder extensionRequestDate(LocalDate extensionRequestDate) {
                        this.extensionRequestDate = extensionRequestDate;
                        return this;
                }

                public ExtensionDetails build() {
                        return new ExtensionDetails(this);
                }
        }
}
