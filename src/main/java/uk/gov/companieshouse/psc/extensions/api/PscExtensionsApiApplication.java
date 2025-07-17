package uk.gov.companieshouse.psc.extensions.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PscExtensionsApiApplication {

    public static final String APPLICATION_NAMESPACE = "psc-extensions-api";

    public static void main( String[] args ) {
        SpringApplication.run( PscExtensionsApiApplication.class, args );
    }

}