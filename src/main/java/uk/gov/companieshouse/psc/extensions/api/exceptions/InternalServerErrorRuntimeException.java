package uk.gov.companieshouse.psc.extensions.api.exceptions;

import static uk.gov.companieshouse.psc.extensions.api.utils.LoggingUtil.LOGGER;
import static uk.gov.companieshouse.psc.extensions.api.utils.RequestContextUtil.getXRequestId;

public class InternalServerErrorRuntimeException extends RuntimeException {

    public InternalServerErrorRuntimeException(final String exceptionMessage, final Exception loggingMessage ) {
        super( exceptionMessage );
        LOGGER.errorContext( getXRequestId(), loggingMessage, null );
    }

}


