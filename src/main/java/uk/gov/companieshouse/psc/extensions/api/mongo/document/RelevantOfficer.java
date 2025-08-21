package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.officer.NameElements;

public class RelevantOfficer {

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String dateOfBirth;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private NameElements nameElements;

        public String getDateOfBirth() {
                return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
                this.dateOfBirth = dateOfBirth;
        }

        public NameElements getNameElements() {
                return nameElements;
        }

        public void setNameElements(NameElements nameElements) {
                this.nameElements = nameElements;
        }
}
