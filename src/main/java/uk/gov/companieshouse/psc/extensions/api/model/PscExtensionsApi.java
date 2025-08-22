package uk.gov.companieshouse.psc.extensions.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.model.common.ResourceLinks;

/**
 * API response object for PSC Extensions.
 */
public class PscExtensionsApi {

    @JsonProperty("etag")
    private String etag;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("links")
    private ResourceLinks links;

    @JsonProperty("data")
    private PscExtensionsData data;

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public ResourceLinks getLinks() {
        return links;
    }

    public void setLinks(ResourceLinks links) {
        this.links = links;
    }

    public PscExtensionsData getData() {
        return data;
    }

    public void setData(PscExtensionsData data) {
        this.data = data;
    }
}