package uk.gov.companieshouse.psc.extensions.api.model;

import java.util.EnumSet;
import java.util.Optional;

public enum FilingKind {

    // filingKind#subKind
    // see Kind getKindFromString(String kind) on chips-filing-consumer
    // java/uk/gov/ch/services/dataholdingobjects/source/filingtype/Kind.java#L14
    PSC_EXTENSION_INDIVIDUAL("psc-extension#psc-extension-individual");

    FilingKind(final String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }

    public static Optional<FilingKind> nameOf(final String value) {
        return EnumSet.allOf(FilingKind.class).stream().filter(v -> v.getValue().equals(value)).findAny();
    }
}