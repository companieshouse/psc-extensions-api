package uk.gov.companieshouse.psc.extensions.api.mongo.converter;

import java.time.OffsetDateTime;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

public class OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {
    @Override
    public Date convert(OffsetDateTime source) {
        return Date.from(source.toInstant());
    }
}
