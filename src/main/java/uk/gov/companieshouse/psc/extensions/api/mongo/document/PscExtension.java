package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.gov.companieshouse.api.model.common.ResourceLinks;

import java.time.Instant;
import java.util.Objects;


@Document(collection = "psc_extensions")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PscExtension {
        @Id
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String id;
        @JsonProperty(value = "created_at",access = JsonProperty.Access.READ_ONLY)
        private Instant createdAt;
        @JsonProperty(value = "updated_at",access = JsonProperty.Access.READ_ONLY)
        private Instant updatedAt;
        @JsonMerge
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private ResourceLinks links;
        @JsonMerge
        @JsonProperty("data")
        private Data data;
        
        // No @JsonMerge: this property MUST NOT be modifiable by PATCH requests
        @JsonProperty("internal_data")
        private InternalData internalData;

        public PscExtension() {
                // required by Spring Data
        }

        public PscExtension(PscExtension other) {
                this.id = other.id;
                this.createdAt = other.createdAt;
                this.updatedAt = other.updatedAt;
                this.links = other.links;
                this.data = other.data;
                this.internalData = other.internalData;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public Instant getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(Instant createdAt) {
                this.createdAt = createdAt;
        }

        public Instant getUpdatedAt() {
                return updatedAt;
        }

        public void setUpdatedAt(Instant updatedAt) {
                this.updatedAt = updatedAt;
        }

        public ResourceLinks getLinks() {
                return links;
        }

        public void setLinks(ResourceLinks links) {
                this.links = links;
        }

        public Data getData() {
                return data;
        }

        public void setData(Data data) {
                this.data = data;
        }

        public InternalData getInternalData() {
                return internalData;
        }

        public void setInternalData(InternalData internalData) {
                this.internalData = internalData;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                PscExtension that = (PscExtension) o;
                return Objects.equals(id, that.id) &&
                        Objects.equals(createdAt, that.createdAt) &&
                        Objects.equals(updatedAt, that.updatedAt) &&
                        Objects.equals(links, that.links) &&
                        Objects.equals(data, that.data) &&
                        Objects.equals(internalData, that.internalData);
        }

        @Override
        public int hashCode() {
                return Objects.hash(id, createdAt, updatedAt, links, data, internalData);
        }

        public static Builder builder() {
                return new Builder();
        }

        public static class Builder {
                private String id;
                private Instant createdAt;
                private Instant updatedAt;
                private ResourceLinks links;
                private Data data;
                private InternalData internalData;

                public Builder id(String id) {
                        this.id = id;
                        return this;
                }

                public Builder createdAt(Instant createdAt) {
                        this.createdAt = createdAt;
                        return this;
                }

                public Builder updatedAt(Instant updatedAt) {
                        this.updatedAt = updatedAt;
                        return this;
                }

                public Builder links(ResourceLinks links) {
                        this.links = links;
                        return this;
                }

                public Builder data(Data data) {
                        this.data = data;
                        return this;
                }

                public Builder internalData(InternalData internalData) {
                        this.internalData = internalData;
                        return this;
                }

                public PscExtension build() {
                        PscExtension pscExtension = new PscExtension();
                        pscExtension.id = this.id;
                        pscExtension.createdAt = this.createdAt;
                        pscExtension.updatedAt = this.updatedAt;
                        pscExtension.links = this.links;
                        pscExtension.data = this.data;
                        pscExtension.internalData = this.internalData;
                        return pscExtension;
                }
        }
}

