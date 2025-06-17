package uk.gov.companieshouse.psc.extensions.exceptions;

import static uk.gov.companieshouse.psc.extensions.utils.LoggingUtil.LOGGER;
import static uk.gov.companieshouse.psc.extensions.utils.RequestContextUtil.getXRequestId;

public class BadRequestRuntimeException extends RuntimeException {

    public BadRequestRuntimeException(final String exceptionMessage, final Exception loggingMessage ) {
        super( exceptionMessage );
        LOGGER.errorContext( getXRequestId(), loggingMessage, null );
    }

}
