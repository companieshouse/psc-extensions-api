package uk.gov.companieshouse.psc.extensions.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.gov.companieshouse.psc.extensions.api.exceptions.InternalServerErrorRuntimeException;

import java.io.IOException;
import java.util.function.Function;

import static uk.gov.companieshouse.psc.extensions.api.utils.LoggingUtil.LOGGER;
import static uk.gov.companieshouse.psc.extensions.api.utils.RequestContextUtil.getXRequestId;

public class ParsingUtil {

    public static <T> Function<String, T> parseJsonTo( final Class<T> clazz ) {
        return json -> {
            final var objectMapper = new ObjectMapper();
            objectMapper.registerModule( new JavaTimeModule() );
            try {
                return objectMapper.readValue( json, clazz );
            } catch ( IOException e ){
                throw new InternalServerErrorRuntimeException( "Unable to parse json", e );
            }
        };
    }

    public static <T> String parseJsonFrom( final T object, final String fallback ) {
        final var objectMapper = new ObjectMapper();
        objectMapper.registerModule( new JavaTimeModule() );
        try {
            return objectMapper.writeValueAsString( object );
        } catch ( IOException exception ) {
            LOGGER.errorContext( getXRequestId(), "Unable to parse json", exception, null );
            return fallback;
        }
    }

}
