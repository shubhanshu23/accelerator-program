package com.adobe.aem.accelerator.program.core.models.solr;

import org.apache.solr.common.SolrDocumentList;
import org.json.JSONObject;

public class SolrSearchResponse {
    private SolrDocumentList solrDocumentList;
    private long total;
    private JSONObject facets;

    public SolrDocumentList getSolrDocumentList() {
        return solrDocumentList;
    }

    public void setSolrDocumentList(SolrDocumentList solrDocumentList) {
        this.solrDocumentList = solrDocumentList;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public JSONObject getFacets() {
        return facets;
    }

    public void setFacets(JSONObject facets) {
        this.facets = facets;
    }
}
