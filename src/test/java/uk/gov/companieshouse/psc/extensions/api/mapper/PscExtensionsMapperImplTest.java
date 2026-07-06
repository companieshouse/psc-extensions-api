package uk.gov.companieshouse.psc.extensions.api.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.Data;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.ExtensionDetails;

@SpringBootTest(classes = {PscExtensionsMapperImpl.class})
public class PscExtensionsMapperImplTest {

    @Autowired
    private PscExtensionsMapperImpl mapper;

    @Test
    void testDataToPscExtensionsData_whenDataIsNull_shouldReturnNull() {
        Data data = null;
        Assertions.assertNull(mapper.dataToPscExtensionsData(data));
    }

    @Test
    void testDataToPscExtensionsData_whenDataIsValid_shouldReturnPscExtensionsData() {
        String companyNumber = "1111";
        String pscNotificationId = "1234";
        ExtensionDetails extensionDetails = new ExtensionDetails();

        Data data = new Data();

        //Set test data
        data.setCompanyNumber(companyNumber);
        data.setPscNotificationId(pscNotificationId);
        data.setExtensionDetails(extensionDetails);

        PscExtensionsData pscExtensionsData = mapper.dataToPscExtensionsData(data);

        //Assert response is not Null
        Assertions.assertNotNull(pscExtensionsData);

        //Assert Data is correctly assigned in converted type
        Assertions.assertEquals(companyNumber, pscExtensionsData.getCompanyNumber());
        Assertions.assertEquals(pscNotificationId, pscExtensionsData.getPscNotificationId());

        //Assert ExtensionDetails are correctly assigned across conversion
        Assertions.assertEquals(extensionDetails.getExtensionReason(), pscExtensionsData.getExtensionDetails().getExtensionReason());
        Assertions.assertEquals(extensionDetails.getExtensionStatus(), pscExtensionsData.getExtensionDetails().getExtensionStatus());
        Assertions.assertEquals(extensionDetails.getExtensionRequestDate(), pscExtensionsData.getExtensionDetails().getExtensionRequestDate());
    }
}
