package uk.gov.companieshouse.psc.extensions.utils;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StaticPropertyUtil {

    @Value( "${spring.application.name}" )
    private String applicationNameSpace;

    public static String APPLICATION_NAMESPACE;

    @PostConstruct
    public void init(){
        StaticPropertyUtil.APPLICATION_NAMESPACE = applicationNameSpace;
    }

}
