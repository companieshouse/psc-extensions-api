package uk.gov.companieshouse.psc.extensions.api.exceptions;

import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.companieshouse.psc.extensions.api.PscExtensionsApiApplication.APPLICATION_NAMESPACE;
import static uk.gov.companieshouse.psc.extensions.api.utils.RequestContextUtil.getXRequestId;

public class ForbiddenRuntimeException extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger( APPLICATION_NAMESPACE );

    public ForbiddenRuntimeException(final String exceptionMessage, final Exception loggingMessage ) {
        super( exceptionMessage );
        LOG.errorContext( getXRequestId(), loggingMessage, null );
    }

}
