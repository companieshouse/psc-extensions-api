package uk.gov.companieshouse.psc.extensions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.companieshouse.psc.extensions.utils.StaticPropertyUtil;

@SpringBootApplication
public class PscExtensionsServiceApplication {

    final StaticPropertyUtil staticPropertyUtil;

    @Autowired
    public PscExtensionsServiceApplication ( final StaticPropertyUtil staticPropertyUtil ) {
        this.staticPropertyUtil = staticPropertyUtil;
    }

    public static void main( String[] args ) {
        SpringApplication.run( PscExtensionsServiceApplication.class, args );
    }

}