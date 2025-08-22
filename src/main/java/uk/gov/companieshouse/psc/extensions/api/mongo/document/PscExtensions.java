package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.model.common.ResourceLinks;
import uk.gov.companieshouse.api.officer.NameElements;

import java.time.Instant;


@Document(collection = "psc_extensions")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PscExtensions {
        @Id
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String id;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private Instant createdAt;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private Instant updatedAt;
        @JsonMerge
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private ResourceLinks links;
        @JsonMerge
        @JsonProperty("data")
        private Data data;

        // No @JsonMerge: this property MUST NOT be modifiable by PATCH requests
        // todo: we dont have any patch requests right? (atm)
        @JsonProperty("internal_data")
        private InternalData internalData;

        public PscExtensions() {
                // required by Spring Data
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
}

