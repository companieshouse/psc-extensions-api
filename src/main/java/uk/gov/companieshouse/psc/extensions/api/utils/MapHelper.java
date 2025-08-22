package uk.gov.companieshouse.psc.extensions.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.util.Map;

public class MapHelper {
    
    private static final ObjectMapper SNAKE_CASE_MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    /**
     * Convert an object to a Map with snake_case property names.
     *
     * @param object the object to convert
     * @return Map representation of the object
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> convertObject(Object object) {
        return SNAKE_CASE_MAPPER.convertValue(object, Map.class);
    }
}