package com.adobe.aem.accelerator.program.core.servlets.solr;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.adobe.aem.accelerator.program.core.services.solr.SolrSearchService;
import com.adobe.aem.accelerator.program.core.services.solr.SolrServerConfigurationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = Servlet.class, immediate = true, property = {
		org.osgi.framework.Constants.SERVICE_DESCRIPTION
				+ "= Delete Indexs from Solr",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=/bin/solr/delete/all/indexes" })

public class DeleteIndexesFromSolr extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(DeleteIndexesFromSolr.class);

	@Reference
	SolrSearchService solrSearchService;

	@Override
    protected void doPost(final SlingHttpServletRequest reqest,
            final SlingHttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSolrClient server = solrSearchService.prepareSolrServer();
		try {
			server.deleteByQuery("*:*");
			server.commit();
			server.close();
			response.getWriter().write("<h3>Deleted all the indexes from solr server </h3>");
		} catch (SolrServerException e) {
			LOG.error("Exception due to", e);
		}
		
        
    }
}
