package uk.gov.companieshouse.psc.extensions.api.controller.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.psc.extensions.api.controller.FilingDataControllerImpl;
import uk.gov.companieshouse.psc.extensions.api.exception.TransactionNotFoundException;
import uk.gov.companieshouse.psc.extensions.api.service.*;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

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


    @Test
    void getFilingsData_whenTransactionMissing_shouldThrowTransactionNotFoundException() {
        // Arrange: request without a "transaction" attribute
        HttpServletRequest req =
                mock(HttpServletRequest.class);
        when(req.getAttribute("transaction")).thenReturn(null);

        RequestContextHolder
                .setRequestAttributes(new ServletRequestAttributes(req));

        FilingDataControllerImpl controller =
                new FilingDataControllerImpl(filingDataService);

        // Act + Assert
        Assertions.assertThrows(
                TransactionNotFoundException.class,
                () -> controller._getFilingsData("tx-404", "filing-404")
        );
    }

    @Test
    void getFilingsData_whenTransactionPresent_shouldReturnOkWithFilingApi() {
        // Arrange: request contains a Transaction attribute
        HttpServletRequest req =
                mock(jakarta.servlet.http.HttpServletRequest.class);

        Transaction tx =
                new Transaction();
        tx.setId("tx-200");

        when(req.getAttribute("transaction")).thenReturn(tx);
        RequestContextHolder
                .setRequestAttributes(new ServletRequestAttributes(req));

        // Prepare FilingApi returned by the service
        FilingApi filing =
                new FilingApi();
        when(filingDataService.generateFilingApi("filing-200", tx)).thenReturn(filing);

        FilingDataControllerImpl controller =
                new FilingDataControllerImpl(filingDataService);

        // Act
        ResponseEntity<List<FilingApi>> response =
                controller._getFilingsData("tx-200", "filing-200");

        // Assert: 200 OK and body contains exactly one FilingApi
        Assertions.assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().size());
        Assertions.assertSame(filing, response.getBody().get(0));

        // Verify service interaction
        verify(filingDataService).generateFilingApi("filing-200", tx);
    }

}