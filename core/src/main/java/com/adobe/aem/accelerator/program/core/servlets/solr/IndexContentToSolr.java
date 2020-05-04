package com.adobe.aem.accelerator.program.core.servlets.solr;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.adobe.aem.accelerator.program.core.services.solr.SolrSearchService;
import com.adobe.aem.accelerator.program.core.services.solr.SolrServerConfigurationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This servlet acts as a bulk update to index content pages and assets to the
 * configured Solr server
 *
 */

@Component(service = Servlet.class, immediate = true, property = {
		org.osgi.framework.Constants.SERVICE_DESCRIPTION
				+ "= Index Content to Solr",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=/bin/solr/push/pages" })

public class IndexContentToSolr extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(IndexContentToSolr.class);

	@Reference
	SolrServerConfigurationService solrConfigurationService;

	@Reference
	SolrSearchService solrSearchService;

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		String indexType = request.getParameter("indexType");
		final String pagesResourcePath = solrConfigurationService
				.getContentPagePath();
		HttpSolrClient server = solrSearchService.prepareSolrServer();
		if (indexType.equalsIgnoreCase("indexpages")) {
			try {
				JSONArray indexPageData = solrSearchService.crawlContent(
						pagesResourcePath, "cq:PageContent");
				boolean resultindexingPages = solrSearchService
						.indexPagesToSolr(indexPageData, server);
				if (resultindexingPages == true) {
					response.getWriter()
							.write("<h3>Successfully indexed content pages to Solr server </h3>");
				} else {
					response.getWriter().write("<h3>Something went wrong</h3>");
				}
			} catch (JSONException | SolrServerException e) {
				LOG.error("Exception due to", e);
				response.getWriter()
						.write("<h3>Something went wrong. Please make sure Solr server is configured properly in Felix</h3>");
			}

		} else {
			response.getWriter().write("<h3>Something went wrong</h3>");
		}

	}

}
