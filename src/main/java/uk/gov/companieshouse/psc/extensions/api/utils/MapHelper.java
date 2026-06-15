package uk.gov.companieshouse.psc.extensions.api.utils;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

public class MapHelper {

    private MapHelper(){

    }
    private static final ObjectMapper SNAKE_CASE_MAPPER = JsonMapper.builder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .build();

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