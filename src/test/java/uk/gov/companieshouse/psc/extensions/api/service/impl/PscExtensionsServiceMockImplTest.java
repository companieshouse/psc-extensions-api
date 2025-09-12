
package uk.gov.companieshouse.psc.extensions.api.service.impl;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.psc.extensions.api.controller.PscExtensionsControllerImpl;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.mongo.repository.PscExtensionsRepository;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PscExtensionServiceMockImplTest {

    @Mock
    private PscExtensionsRepository repository;

    @Mock
    private PscExtensionsService pscExtensionsService;

    @Mock
    private PscExtensionsServiceImpl pscExtensionsServiceImpl;

    @Mock
    private PscExtensionsControllerImpl pscExtensionsControllerImpl;


    @InjectMocks
    private PscExtensionsServiceImpl service;

    private final String pscNotificationId = "PSC123";

//    @Test
//    public void testGetCount_whenDocumentsExist_returnsCount() {
//        long expectedCount = 2;
//
//        when(service.getExtensionCount(pscNotificationId)).thenReturn(Optional.of(expectedCount));
//
//        ResponseEntity<Long> response = pscExtensionsControllerImpl.getPscExtensionCount(pscNotificationId);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(expectedCount, response.getBody());
//        verify(service, times(1)).getExtensionCount(expectedCount);
//    }

//    @Test
//    public void testGetCount_whenNoDocuments_throwsException() {
//        String testId = "456";
//
//        when(service.getDocCount(testId)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            controller.getCount(testId);
//        });
//
//        assertEquals("No documents found for the given ID.", exception.getMessage());
//        verify(service, times(1)).getDocCount(testId);
//    }

    @Test
    void shouldThrowExceptionWhenNotificationIdIsNull() {
        assertThrows(NullPointerException.class, () -> service.getExtensionCount(null));
    }

    @Test
    void shouldThrowExceptionWhenNotificationIdIsEmpty() {
        assertThrows(NullPointerException.class, () -> service.getExtensionCount(""));
    }

    @Test
    void shouldLogWarningWhenMultipleExtensionsFound() throws IllegalArgumentException{
        when(repository.countByDataPscNotificationId(pscNotificationId)).thenReturn(2L);

        assertThrows(IllegalArgumentException.class, ()-> {
            service.getExtensionCount(pscNotificationId);
        });
    }

    @Test
    void shouldReturnEmptyOptionalWhenExtensionNotFound() {
        when(repository.countByDataPscNotificationId(pscNotificationId)).thenReturn(1L);
        when(repository.findById(pscNotificationId)).thenReturn(Optional.empty());

        Optional<Long> result = service.getExtensionCount(pscNotificationId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnExtensionWhenFound() {
        PscExtension extension = new PscExtension();
        when(repository.countByDataPscNotificationId(pscNotificationId)).thenReturn(1L);
        when(repository.findById(pscNotificationId)).thenReturn(Optional.of(extension));

        Optional<Long> result = service.getExtensionCount(pscNotificationId);

        assertTrue(result.isPresent());
        assertEquals(extension, result.get());
    }
}