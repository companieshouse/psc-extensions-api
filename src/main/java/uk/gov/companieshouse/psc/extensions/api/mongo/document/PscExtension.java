package uk.gov.companieshouse.psc.extensions.api.mongo.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Objects;

import uk.gov.companieshouse.api.model.common.ResourceLinks;

@Document(collection = "psc_extensions")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PscExtension {
        @Id
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String id;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private LocalDateTime createdAt;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private LocalDateTime updatedAt;
        @JsonMerge
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private ResourceLinks links;
        @JsonMerge
        @JsonProperty("data")
        private Data data;

        @Override
        public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) return false;
                PscExtension that = (PscExtension) o;
                return Objects.equals(id, that.id) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(links, that.links) && Objects.equals(data, that.data);
        }

        @Override
        public int hashCode() {
                return Objects.hash(id, createdAt, updatedAt, links, data);
        }


        public PscExtension(String id, ResourceLinks links, LocalDateTime updatedAt, LocalDateTime createdAt, Data data) {
                this.id = id;
                this.links = links;
                this.updatedAt = updatedAt;
                this.createdAt = createdAt;
                this.data = data;
        }

        public PscExtension() {
                // required by Spring Data
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public LocalDateTime getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
                return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
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
}

