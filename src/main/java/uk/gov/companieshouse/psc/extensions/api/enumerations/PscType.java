package uk.gov.companieshouse.psc.extensions.api.enumerations;

public enum PscType {
    INDIVIDUAL("individual"),
    CORPORATE("corporate");

    private final String value;

    PscType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}