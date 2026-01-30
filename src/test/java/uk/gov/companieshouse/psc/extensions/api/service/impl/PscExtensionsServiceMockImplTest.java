package uk.gov.companieshouse.psc.extensions.api.service.impl;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.mongo.repository.PscExtensionsRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PscExtensionServiceMockImplTest {

    @Mock
    private PscExtensionsRepository repository;
    @Mock
    private Logger logger;

    @InjectMocks
    private PscExtensionsServiceImpl service;

    private final String pscNotificationId = "PSC123";

    @Test
    void shouldReturnNumberOfExtensionsRequestWhenCalled() {
        long expectedCount = 1;

        when(repository.countByDataPscNotificationId(pscNotificationId)).thenReturn(expectedCount);

        Optional<Long> response = service.getExtensionCount(pscNotificationId);

        assertTrue(response.isPresent());
        assertEquals(1L, response.get());
    }
    @Test
    void shouldReturnEmptyOptionalWhenCountIsZero() {
        when(repository.countByDataPscNotificationId(pscNotificationId)).thenReturn(0L);

        Optional<Long> result = service.getExtensionCount(pscNotificationId);

        assertTrue(result.isEmpty());
    }
}