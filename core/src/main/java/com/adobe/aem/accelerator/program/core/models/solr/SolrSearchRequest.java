package com.adobe.aem.accelerator.program.core.models.solr;

import org.apache.solr.client.solrj.SolrQuery;

public class SolrSearchRequest {
	private String text;
	private String filters;
	private String sort;
	private SolrQuery.ORDER sortOrder = SolrQuery.ORDER.asc;
	private Boolean facets;


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public SolrQuery.ORDER getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SolrQuery.ORDER sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Boolean getFacets() {
		return facets;
	}

	public void setFacets(Boolean facets) {
		this.facets = facets;
	}
}
