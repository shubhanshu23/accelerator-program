package com.adobe.aem.accelerator.program.core.services.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;

public interface SolrQueryCommand {

    void setText(String text, SolrQuery solrQuery);

    void setSort(String sort, ORDER sortOrder, SolrQuery solrQuery);

    void setRows(int rows, SolrQuery solrQuery);

    void setFilters(String filters, SolrQuery solrQuery);

}
