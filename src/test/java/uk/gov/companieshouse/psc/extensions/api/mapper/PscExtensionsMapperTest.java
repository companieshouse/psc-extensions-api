package uk.gov.companieshouse.psc.extensions.api.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionResponse;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;

@SpringBootTest
class PscExtensionsMapperTest {

    @Autowired
    private PscExtensionsMapper mapper;

    @Test
    void testToEntity() {
        PscExtensionsData input = new PscExtensionsData();
        input.setRequesterEmail("example@example.com");

        PscExtension result = mapper.toEntity(input);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getData());
        Assertions.assertEquals("example@example.com", result.getData().getSensitiveData().getRequesterEmail());
    }

    @Test
    void testToApi() {
        PscExtension extension = new PscExtension();

        PscExtensionResponse response = mapper.toApi(extension);

        Assertions.assertNotNull(response);
        Assertions.assertNull(response.getEtag());
        Assertions.assertNull(response.getKind());
    }

}
