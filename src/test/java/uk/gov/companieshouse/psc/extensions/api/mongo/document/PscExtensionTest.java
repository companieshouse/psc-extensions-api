package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.common.ResourceLinks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

class PscExtensionTest {

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
    void testEquals_identicalObjects_shouldReturnTrue() {
        PscExtension ext1 = createSamplePscExtension();
        PscExtension ext2 = createSamplePscExtension();
        assertEquals(ext1, ext2);
        assertEquals(ext2, ext1); // symmetry
    }

    @Test
    void testEquals_differentId_shouldReturnFalse() {
        PscExtension ext1 = createSamplePscExtension();
        PscExtension ext2 = createSamplePscExtension();
        ext2.setId("different-id");
        assertNotEquals(ext1, ext2);
    }

    @Test
    void testEquals_null_shouldReturnFalse() {
        PscExtension ext = createSamplePscExtension();
        Assertions.assertNotNull(ext);
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

    private PscExtension createSamplePscExtension() {
        PscExtension ext = new PscExtension();
        ext.setId("123");
        ext.setCreatedAt(Instant.parse("2025-09-10T14:44:28.477Z"));
        ext.setUpdatedAt(Instant.parse("2025-09-10T14:44:28.477Z"));
        return ext;
    }


    @Test
    void testBuilder_buildsObjectWithAllFields() throws URISyntaxException {
        String id = "builder-id";
        Instant now = Instant.now();
        ResourceLinks links = new ResourceLinks(new URI("https://example.com/links"),
                new URI("https://example.com/self"));
        Data data = new Data();
        InternalData internalData = new InternalData();

        PscExtension built = PscExtension.builder()
                .id(id)
                .createdAt(now)
                .updatedAt(now)
                .links(links)
                .data(data)
                .internalData(internalData)
                .build();

        assertThat(built).isNotNull();
        assertThat(built.getId()).isEqualTo(id);
        assertThat(built.getCreatedAt()).isEqualTo(now);
        assertThat(built.getUpdatedAt()).isEqualTo(now);
        assertThat(built.getLinks()).isEqualTo(links);
        assertThat(built.getData()).isEqualTo(data);
        assertThat(built.getInternalData()).isEqualTo(internalData);
    }

    @Test
    void testCopyConstructor_copiesAllFields() throws URISyntaxException {
        PscExtension original = new PscExtension();
        original.setId("orig-id");
        Instant created = Instant.parse("2025-01-01T00:00:00Z");
        Instant updated = Instant.parse("2025-01-02T00:00:00Z");
        original.setCreatedAt(created);
        original.setUpdatedAt(updated);
        ResourceLinks links = new ResourceLinks(new URI("https://example.com/links"),
                new URI("https://example.com/self"));
        original.setLinks(links);
        Data data = new Data();
        original.setData(data);
        InternalData internal = new InternalData();
        original.setInternalData(internal);

        PscExtension copy = new PscExtension(original);

        assertThat(copy).isNotNull();
        assertThat(copy.getId()).isEqualTo(original.getId());
        assertThat(copy.getCreatedAt()).isEqualTo(original.getCreatedAt());
        assertThat(copy.getUpdatedAt()).isEqualTo(original.getUpdatedAt());
        assertThat(copy.getLinks()).isEqualTo(original.getLinks());
        assertThat(copy.getData()).isEqualTo(original.getData());
        assertThat(copy.getInternalData()).isEqualTo(original.getInternalData());
        // equals/hashCode contract for identical field values
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
    }

    @Test
    void testEquals_nullExplicit_shouldReturnFalse() {
        PscExtension ext = createSamplePscExtension();
        // equals(null) must be false
        assertNotEquals(null, ext);
    }
}