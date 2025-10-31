package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Data {

        @JsonProperty("company_number")
        private String companyNumber;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String pscNotificationId;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private SensitiveData sensitiveData;
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

        public SensitiveData getSensitiveData() {
            return sensitiveData;
        }
    
        public void setSensitiveData(SensitiveData sensitiveData) {
            this.sensitiveData = sensitiveData;
        }

        public ExtensionDetails getExtensionDetails() {
                return extensionDetails;
        }

        public void setExtensionDetails(ExtensionDetails extensionDetails) {
                this.extensionDetails = extensionDetails;
        }

        @Override
        public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) return false;
                Data data = (Data) o;
                return Objects.equals(companyNumber, data.companyNumber) && Objects.equals(pscNotificationId, data.pscNotificationId) && Objects.equals(extensionDetails, data.extensionDetails);
        }

        @Override
        public int hashCode() {
                return Objects.hash(companyNumber, pscNotificationId, extensionDetails);
        }

}
