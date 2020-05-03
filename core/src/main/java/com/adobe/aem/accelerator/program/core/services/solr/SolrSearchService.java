package com.adobe.aem.accelerator.program.core.services.solr;

import java.io.IOException;

import javax.jcr.RepositoryException;

import com.adobe.aem.accelerator.program.core.models.solr.SolrSearchRequest;
import com.adobe.aem.accelerator.program.core.models.solr.SolrSearchResponse;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import com.day.cq.search.result.SearchResult;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface SolrSearchService {

	String createQueryURL();

	JSONArray crawlContent(String resourcePath, String resourceType);

	JSONArray createPageMetadataArray(SearchResult results)
			throws RepositoryException;

	JSONObject createPageMetadataObject(Resource pageContent);

	boolean indexPageToSolr(JSONObject indexPageData, HttpSolrClient server)
			throws JSONException, SolrServerException, IOException;

	boolean indexPagesToSolr(JSONArray indexPageData, HttpSolrClient server)
			throws JSONException, SolrServerException, IOException;

	 QueryResponse executeSearch(SolrSearchRequest searchRequest);

	 SolrSearchResponse performFacetSearch(SlingHttpServletRequest request, SlingHttpServletResponse response);
}
