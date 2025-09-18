package uk.gov.companieshouse.psc.extensions.api.controller.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.psc.extensions.api.controller.FilingDataControllerImpl;
import uk.gov.companieshouse.psc.extensions.api.service.*;


import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class FilingDataControllerImplTest {

    @Mock
    private FilingDataService filingDataService;


    @Test
    void constructor_ShouldSetDependencies() {
        FilingDataControllerImpl testController = new FilingDataControllerImpl(
                filingDataService);
        assertNotNull(testController);
    }
}