package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Data {

        @JsonProperty("company_number")
        private String companyNumber;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String pscNotificationId;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private ExtensionDetails extensionDetails;

        public String getCompanyNumber() {
                return companyNumber;
        }

        public void setCompanyNumber(String companyNumber) {
                this.companyNumber = companyNumber;
        }

        public String getPscNotificationId() {
                return pscNotificationId;
        }

        public void setPscNotificationId(String pscNotificationId) {
                this.pscNotificationId = pscNotificationId;
        }

        public ExtensionDetails getExtensionDetails() {
                return extensionDetails;
        }

        public void setExtensionDetails(ExtensionDetails extensionDetails) {
                this.extensionDetails = extensionDetails;
        }
}
