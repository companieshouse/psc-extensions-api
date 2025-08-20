package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

public class Data {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String companyNumber;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String pscAppointmentId;
        @Field("relevant_officer")
        private RelevantOfficer relevantOfficer;
        @Field("extension_details")
        private ExtensionDetails extensionDetails;

        public String getCompanyNumber() {
                return companyNumber;
        }

        public void setCompanyNumber(String companyNumber) {
                this.companyNumber = companyNumber;
        }

        public String getPscAppointmentId() {
                return pscAppointmentId;
        }

        public void setPscAppointmentId(String pscAppointmentId) {
                this.pscAppointmentId = pscAppointmentId;
        }

        public RelevantOfficer getRelevantOfficer() {
                return relevantOfficer;
        }

        public void setRelevantOfficer(RelevantOfficer relevantOfficer) {
                this.relevantOfficer = relevantOfficer;
        }

        public ExtensionDetails getExtensionDetails() {
                return extensionDetails;
        }

        public void setExtensionDetails(ExtensionDetails extensionDetails) {
                this.extensionDetails = extensionDetails;
        }
}
