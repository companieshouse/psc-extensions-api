package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;

public class ExtensionDetails {

        @JsonProperty(value = "extension_reason", access = JsonProperty.Access.READ_ONLY)
        private String extensionReason;
        @JsonProperty(value = "extension_status", access = JsonProperty.Access.READ_ONLY)
        private String extensionStatus;
        @JsonProperty(value = "extension_request_date", access = JsonProperty.Access.READ_ONLY)
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

        @Override
        public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) return false;
                ExtensionDetails that = (ExtensionDetails) o;
                return Objects.equals(extensionReason, that.extensionReason) && Objects.equals(extensionStatus, that.extensionStatus) && Objects.equals(extensionRequestDate, that.extensionRequestDate);
        }

        @Override
        public int hashCode() {
                return Objects.hash(extensionReason, extensionStatus, extensionRequestDate);
        }

        @Override
        public String toString() {
                return "ExtensionDetails{" +
                        "extensionReason='" + extensionReason + '\'' +
                        ", extensionStatus='" + extensionStatus + '\'' +
                        ", extensionRequestDate=" + extensionRequestDate +
                        '}';
        }
}
