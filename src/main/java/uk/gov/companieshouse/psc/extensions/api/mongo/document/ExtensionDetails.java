package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtensionDetails {

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String extensionReason;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String extensionStatus;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String extensionRequestDate;

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

        public String getExtensionRequestDate() {
                return extensionRequestDate;
        }

        public void setExtensionRequestDate(String extensionRequestDate) {
                this.extensionRequestDate = extensionRequestDate;
        }
}
