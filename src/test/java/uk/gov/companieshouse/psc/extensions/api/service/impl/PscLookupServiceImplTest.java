package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.sdk.companieshouse.InternalApiClientService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PscLookupServiceImplTest {

    private static final String TRANSACTION_ID = "test-transaction-id";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String PSC_NOTIFICATION_ID = "test-psc-notification-id";
    @Mock
    private InternalApiClientService internalApiClientService;
    @Mock
    private Logger logger;
    @InjectMocks
    private PscLookupServiceImpl pscLookupService;
    private Transaction testTransaction;
    private PscExtensionsData testData;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setId(TRANSACTION_ID);

        testData = new PscExtensionsData();
        testData.setCompanyNumber(COMPANY_NUMBER);
        testData.setPscNotificationId(PSC_NOTIFICATION_ID);
    }

    @Test
    void constructor_ShouldSetDependencies() {
        PscLookupServiceImpl service = new PscLookupServiceImpl(internalApiClientService, logger);
        assertNotNull(service);
    }

    @Test
    void serviceFields_ShouldNotBeNull() {
        assertNotNull(pscLookupService);
    }


    @Test
    void getPscIndividualFullRecord_success_returnsData() throws Exception {
        // Arrange
        String companyNumber = "12345678";
        String pscId = "psc-123";
        uk.gov.companieshouse.psc.extensions.api.enumerations.PscType pscType =
                uk.gov.companieshouse.psc.extensions.api.enumerations.PscType.INDIVIDUAL;

        uk.gov.companieshouse.api.InternalApiClient internalApiClient =
                org.mockito.Mockito.mock(uk.gov.companieshouse.api.InternalApiClient.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);

        uk.gov.companieshouse.api.psc.IndividualFullRecord fullRecord =
                new uk.gov.companieshouse.api.psc.IndividualFullRecord();

        // Deep-stub the full chain to return the fullRecord from getData()
        org.mockito.Mockito.when(
                internalApiClient.privatePscFullRecordResourceHandler()
                        .getPscFullRecord("/company/" + companyNumber + "/persons-with-significant-control/" + pscType.getValue() + "/" + pscId + "/full_record")
                        .execute()
                        .getData()
        ).thenReturn(fullRecord);

        org.mockito.Mockito.when(internalApiClientService.getInternalApiClient()).thenReturn(internalApiClient);

        // Act
        uk.gov.companieshouse.api.psc.IndividualFullRecord result =
                pscLookupService.getPscIndividualFullRecord(companyNumber, pscId, pscType);

        // Assert
        org.junit.jupiter.api.Assertions.assertNotNull(result);
    }

    @Test
    void getPscIndividualFullRecord_apiError404_throwsFilingResourceNotFoundException() throws Exception {
        // Arrange
        String companyNumber = "12345678";
        String pscId = "psc-404";
        uk.gov.companieshouse.psc.extensions.api.enumerations.PscType pscType =
                uk.gov.companieshouse.psc.extensions.api.enumerations.PscType.INDIVIDUAL;

        uk.gov.companieshouse.api.InternalApiClient internalApiClient =
                org.mockito.Mockito.mock(uk.gov.companieshouse.api.InternalApiClient.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);

        // Throw ApiErrorResponseException (404) from execute()
        org.mockito.Mockito.when(
                internalApiClient.privatePscFullRecordResourceHandler()
                        .getPscFullRecord("/company/" + companyNumber + "/persons-with-significant-control/" + pscType.getValue() + "/" + pscId + "/full_record")
                        .execute()
        ).thenThrow(
                new uk.gov.companieshouse.api.error.ApiErrorResponseException(
                        new com.google.api.client.http.HttpResponseException.Builder(
                                org.springframework.http.HttpStatus.NOT_FOUND.value(),
                                "Not Found",
                                new com.google.api.client.http.HttpHeaders()
                        )
                )
        );

        org.mockito.Mockito.when(internalApiClientService.getInternalApiClient()).thenReturn(internalApiClient);

        // Act + Assert
        org.junit.jupiter.api.Assertions.assertThrows(
                uk.gov.companieshouse.psc.extensions.api.exception.FilingResourceNotFoundException.class,
                () -> pscLookupService.getPscIndividualFullRecord(companyNumber, pscId, pscType)
        );
    }

    @Test
    void getPscIndividualFullRecord_apiError500_throwsPscLookupServiceException() throws Exception {
        // Arrange
        String companyNumber = "12345678";
        String pscId = "psc-500";
        uk.gov.companieshouse.psc.extensions.api.enumerations.PscType pscType =
                uk.gov.companieshouse.psc.extensions.api.enumerations.PscType.INDIVIDUAL;

        uk.gov.companieshouse.api.InternalApiClient internalApiClient =
                org.mockito.Mockito.mock(uk.gov.companieshouse.api.InternalApiClient.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);

        // Throw ApiErrorResponseException (500) from execute()
        org.mockito.Mockito.when(
                internalApiClient.privatePscFullRecordResourceHandler()
                        .getPscFullRecord("/company/" + companyNumber + "/persons-with-significant-control/" + pscType.getValue() + "/" + pscId + "/full_record")
                        .execute()
        ).thenThrow(
                new uk.gov.companieshouse.api.error.ApiErrorResponseException(
                        new com.google.api.client.http.HttpResponseException.Builder(
                                500, "Internal Error", new com.google.api.client.http.HttpHeaders()
                        )
                )
        );

        org.mockito.Mockito.when(internalApiClientService.getInternalApiClient()).thenReturn(internalApiClient);

        // Act + Assert
        org.junit.jupiter.api.Assertions.assertThrows(
                uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException.class,
                () -> pscLookupService.getPscIndividualFullRecord(companyNumber, pscId, pscType)
        );
    }

    @Test
    void getPscIndividualFullRecord_uriValidation_throwsPscLookupServiceException() throws Exception {
        // Arrange
        String companyNumber = "12345678";
        String pscId = "psc-uri";
        uk.gov.companieshouse.psc.extensions.api.enumerations.PscType pscType =
                uk.gov.companieshouse.psc.extensions.api.enumerations.PscType.INDIVIDUAL;

        uk.gov.companieshouse.api.InternalApiClient internalApiClient =
                org.mockito.Mockito.mock(uk.gov.companieshouse.api.InternalApiClient.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);

        // Throw URIValidationException from execute()
        org.mockito.Mockito.when(
                internalApiClient.privatePscFullRecordResourceHandler()
                        .getPscFullRecord("/company/" + companyNumber + "/persons-with-significant-control/" + pscType.getValue() + "/" + pscId + "/full_record")
                        .execute()
        ).thenThrow(new uk.gov.companieshouse.api.handler.exception.URIValidationException("bad uri"));

        org.mockito.Mockito.when(internalApiClientService.getInternalApiClient()).thenReturn(internalApiClient);

        // Act + Assert
        org.junit.jupiter.api.Assertions.assertThrows(
                uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException.class,
                () -> pscLookupService.getPscIndividualFullRecord(companyNumber, pscId, pscType)
        );

    }

}
