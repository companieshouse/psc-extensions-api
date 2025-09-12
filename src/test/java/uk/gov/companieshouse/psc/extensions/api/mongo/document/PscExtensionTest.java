package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.common.ResourceLinks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

public class PscExtensionTest {

    @Test
    void testDefaultConstructor() {
        PscExtension extension = new PscExtension();
        assertThat(extension).isNotNull();
        assertThat(extension.getId()).isNull();
        assertThat(extension.getCreatedAt()).isNull();
        assertThat(extension.getUpdatedAt()).isNull();
        assertThat(extension.getLinks()).isNull();
        assertThat(extension.getData()).isNull();
        assertThat(extension.getInternalData()).isNull();
    }

    @Test
    void testSettersAndGetters() throws URISyntaxException {
        PscExtension extension = new PscExtension();

        String id = "abc123";
        Instant now = Instant.now();
        ResourceLinks links = new ResourceLinks(new URI("https://example.com"),
                new URI("https://example.com"));
        Data data = new Data();
        InternalData internalData = new InternalData();

        extension.setId(id);
        extension.setCreatedAt(now);
        extension.setUpdatedAt(now);
        extension.setLinks(links);
        extension.setData(data);
        extension.setInternalData(internalData);

        assertThat(extension.getId()).isEqualTo(id);
        assertThat(extension.getCreatedAt()).isEqualTo(now);
        assertThat(extension.getUpdatedAt()).isEqualTo(now);
        assertThat(extension.getLinks()).isEqualTo(links);
        assertThat(extension.getData()).isEqualTo(data);
        assertThat(extension.getInternalData()).isEqualTo(internalData);
    }

    @Test
    void testEquals_sameObject_shouldReturnTrue() {
        PscExtension ext = createSamplePscExtension();
        assertTrue(ext.equals(ext));
    }

    @Test
    void testEquals_identicalObjects_shouldReturnTrue() {
        PscExtension ext1 = createSamplePscExtension();
        PscExtension ext2 = createSamplePscExtension();
        assertTrue(ext1.equals(ext2));
        assertTrue(ext2.equals(ext1)); // symmetry
    }

    @Test
    void testEquals_differentId_shouldReturnFalse() {
        PscExtension ext1 = createSamplePscExtension();
        PscExtension ext2 = createSamplePscExtension();
        ext2.setId("different-id");
        assertFalse(ext1.equals(ext2));
    }

    @Test
    void testEquals_null_shouldReturnFalse() {
        PscExtension ext = createSamplePscExtension();
        assertFalse(ext.equals(null));
    }

    @Test
    void testEquals_differentClass_shouldReturnFalse() {
        PscExtension ext = createSamplePscExtension();
        assertFalse(ext.equals("not-an-extension"));
    }
    private PscExtension createSamplePscExtension() {
        PscExtension ext = new PscExtension();
        ext.setId("123");
        ext.setCreatedAt(Instant.parse("2025-09-10T14:44:28.477Z"));
        ext.setUpdatedAt(Instant.parse("2025-09-10T14:44:28.477Z"));
        return ext;
    }

    @Test
    void testHashCode_consistency() {
        PscExtension ext = createSamplePscExtension();
        int hash1 = ext.hashCode();
        int hash2 = ext.hashCode();
        assertEquals(hash1, hash2, "Hash code should be consistent");
    }

    @Test
    void testHashCode_equalObjects_shouldHaveSameHashCode() {
        PscExtension ext1 = createSamplePscExtension();
        PscExtension ext2 = createSamplePscExtension();
        assertEquals(ext1.hashCode(), ext2.hashCode(), "Equal objects must have equal hash codes");
    }

    @Test
    void testHashCode_differentObjects_shouldHaveDifferentHashCodes() {
        PscExtension ext1 = createSamplePscExtension();
        PscExtension ext2 = createSamplePscExtension();
        ext2.setId("different-id");
        assertNotEquals(ext1.hashCode(), ext2.hashCode(), "Different objects should have different hash codes");
    }

    private PscExtension createSampleExtension() {
        return PscExtension.builder()
                .id("123")
                .createdAt(Instant.parse("2025-09-10T14:44:28.477Z"))
                .updatedAt(Instant.parse("2025-09-10T14:44:28.477Z"))
                .build();
    }
}