package uk.gov.companieshouse.psc.extensions.api.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionResponse;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;

@SpringBootTest
public class PscExtensionsMapperTest {

    @Autowired
    private PscExtensionsMapper mapper;

    @Test
    public void testToEntity() {
        PscExtensionsData input = new PscExtensionsData();

        PscExtension result = mapper.toEntity(input);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getData());
    }

    @Test
    public void testToApi() {
        PscExtension extension = new PscExtension();

        PscExtensionResponse response = mapper.toApi(extension);

        Assertions.assertNotNull(response);
        Assertions.assertNull(response.getEtag());
        Assertions.assertNull(response.getKind());
    }

}
