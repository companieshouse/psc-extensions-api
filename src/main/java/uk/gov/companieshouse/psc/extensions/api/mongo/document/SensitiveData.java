package uk.gov.companieshouse.psc.extensions.api.mongo.document;

public class SensitiveData {

    private String requesterEmail;

    public SensitiveData(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }
}
