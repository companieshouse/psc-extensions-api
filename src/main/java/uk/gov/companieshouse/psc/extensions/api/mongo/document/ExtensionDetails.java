package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import org.springframework.data.mongodb.core.mapping.Field;

public class ExtensionDetails {

        @Field("extensions_reason")
        private String extensionReason;

        @Field("extension_status")
        private String extensionStatus;

        @Field("extension_request_date")
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
