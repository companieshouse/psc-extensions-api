package uk.gov.companieshouse.psc.extensions.api.model;

public class FilingKind {

    public static final String KIND = "psc-extension";
    private static final String SUBKIND = "psc-extension-individual";
    public static final String FULL_KIND = KIND + SUBKIND;

    private FilingKind() {
        // empty constructor
    }

}