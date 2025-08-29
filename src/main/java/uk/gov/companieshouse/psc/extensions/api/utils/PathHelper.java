package uk.gov.companieshouse.psc.extensions.api.utils;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PathHelper {

    public static List<String> getAllPathsFromInterfaces(Class<?>... interfaceClasses) {
        return Arrays.stream(interfaceClasses)
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods()))
                .map(method -> method.getAnnotation(RequestMapping.class))
                .filter(Objects::nonNull)
                .flatMap(annotation -> Arrays.stream(annotation.value()))
                .toList();
    }

}
