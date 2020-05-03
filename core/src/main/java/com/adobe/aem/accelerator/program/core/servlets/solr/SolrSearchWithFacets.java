package com.adobe.aem.accelerator.program.core.servlets.solr;


import com.adobe.aem.accelerator.program.core.models.solr.SolrSearchResponse;
import com.adobe.aem.accelerator.program.core.services.solr.SolrSearchService;
import com.adobe.aem.accelerator.program.core.services.solr.SolrServerConfigurationService;
import com.adobe.aem.accelerator.program.core.utils.solr.SolrSearchConstants;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;


@Component(service = Servlet.class, immediate = true, property = {
        org.osgi.framework.Constants.SERVICE_DESCRIPTION
                + "= Index Content to Solr",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=/bin/solr/facets" })

public class SolrSearchWithFacets extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(SolrSearchWithFacets.class);

    @Reference
    SolrServerConfigurationService solrConfigurationService;

    @Reference
    SolrSearchService solrSearchService;

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        SolrSearchResponse searchResponse = solrSearchService.performFacetSearch(request, response);
        String finalSearchResponse = gson.toJson(searchResponse);

        response.setContentType(SolrSearchConstants.CONTENT_TYPE);
        response.setCharacterEncoding(SolrSearchConstants.UTF_ENCODING);
        response.getWriter().write(finalSearchResponse);

    }
}
