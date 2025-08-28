package uk.gov.companieshouse.psc.extensions.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.ExtensionDetails;

public class PscExtensionsData {

    @NotBlank(message = "Company number is required")
    @JsonProperty("company_number")
    private String companyNumber;

    @NotBlank(message = "PSC notification ID is required")
    @JsonProperty("psc_notification_id")
    private String pscNotificationId;

    @NotNull(message = "Extension details are required")
    @JsonProperty("extension_details")
    private ExtensionDetails extensionDetails;

    public PscExtensionsData() {
        // Default constructor for Jackson
    }

    private PscExtensionsData(Builder builder) {
        this.companyNumber = builder.companyNumber;
        this.pscNotificationId = builder.pscNotificationId;
        this.extensionDetails = builder.extensionDetails;
    }

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

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(PscExtensionsData copy) {
        Builder builder = new Builder();
        builder.companyNumber = copy.companyNumber;
        builder.pscNotificationId = copy.pscNotificationId;
        builder.extensionDetails = copy.extensionDetails;
        return builder;
    }

    public static class Builder {
        private String companyNumber;
        private String pscNotificationId;
        private ExtensionDetails extensionDetails;

        public Builder companyNumber(String companyNumber) {
            this.companyNumber = companyNumber;
            return this;
        }

        public Builder pscNotificationId(String pscNotificationId) {
            this.pscNotificationId = pscNotificationId;
            return this;
        }


        public Builder extensionDetails(ExtensionDetails extensionDetails) {
            this.extensionDetails = extensionDetails;
            return this;
        }

        public PscExtensionsData build() {
            return new PscExtensionsData(this);
        }
    }
}