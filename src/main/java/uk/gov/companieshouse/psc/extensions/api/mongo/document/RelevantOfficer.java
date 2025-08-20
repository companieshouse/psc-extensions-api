package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.officer.NameElements;

public class RelevantOfficer {

        @Field("date_of_birth")
        private String dateOfBirth;

        @Field("name_elements")
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
