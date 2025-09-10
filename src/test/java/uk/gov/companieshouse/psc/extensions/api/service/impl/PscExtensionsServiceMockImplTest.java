
package uk.gov.companieshouse.psc.extensions.api.service.impl;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.mongo.repository.PscExtensionsRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PscExtensionServiceMockImplTest {
    @Mock
    private PscExtensionsRepository repository;


    @InjectMocks
    private PscExtensionsServiceImpl service;

    private final String pscNotificationId = "PSC123";

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

        Optional<PscExtension> result = service.getExtensionCount(pscNotificationId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnExtensionWhenFound() {
        PscExtension extension = new PscExtension();
        when(repository.countByDataPscNotificationId(pscNotificationId)).thenReturn(1L);
        when(repository.findById(pscNotificationId)).thenReturn(Optional.of(extension));

        Optional<PscExtension> result = service.getExtensionCount(pscNotificationId);

        assertTrue(result.isPresent());
        assertEquals(extension, result.get());
    }
}