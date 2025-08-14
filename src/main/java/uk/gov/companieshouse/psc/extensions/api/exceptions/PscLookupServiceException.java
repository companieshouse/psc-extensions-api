package uk.gov.companieshouse.psc.extensions.api.exceptions;

/**
 * PSC Service query failed.
 */
public class PscLookupServiceException extends RuntimeException {

    public PscLookupServiceException(final String s, final Exception e) {
        super(s, e);
    }
}
