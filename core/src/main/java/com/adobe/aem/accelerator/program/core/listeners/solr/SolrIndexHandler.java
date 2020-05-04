package com.adobe.aem.accelerator.program.core.listeners.solr;

import com.adobe.aem.accelerator.program.core.services.solr.SolrSearchService;
import com.adobe.aem.accelerator.program.core.services.solr.SolrServerConfigurationService;
import com.adobe.aem.accelerator.program.core.utils.solr.SolrSearchConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component(service= EventHandler.class, immediate=true,
        property = {
                EventConstants.EVENT_TOPIC +  "=" + PageEvent.EVENT_TOPIC
        }
)

public class SolrIndexHandler implements EventHandler {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Reference
    org.apache.sling.jcr.api.SlingRepository repository;

    @Reference
    SolrServerConfigurationService solrConfigurationService;

    @Reference
    SolrSearchService solrSearchService;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public void handleEvent(Event event) {
        log.info("Inside handleEvent() ");
        ResourceResolver resolver = null;
        try {
            resolver = getResourceResolver();
            PageEvent pageEvent = PageEvent.fromEvent(event);

            if (null != pageEvent) {
                Iterator<PageModification> modifications = pageEvent.getModifications();
                while(modifications.hasNext()){
                    PageModification pMod = modifications.next();
                    PageModification.ModificationType type = pMod.getType();
                    String pagePath = pMod.getPath();
                    Resource res = resolver.getResource(pagePath);
                    HttpSolrClient server = solrSearchService.prepareSolrServer();
                    SolrQuery query = new SolrQuery();
                    query.set(SolrSearchConstants.SOLR_QUERY_Q,
                            SolrSearchConstants.SOLRDOC_FIELD_ID + ":\"" + pagePath + "\"");

                    // CREATED/MODIFIED EVENT
                    if ((type == PageModification.ModificationType.MODIFIED) ||
                            (type == PageModification.ModificationType.CREATED)) {
                        updateIndex(server, query, res);
                    }

                    // REMOVED EVENT
                    else if (type == PageModification.ModificationType.DELETED ||
                            type == PageModification.ModificationType.VERSION_CREATED) {
                        deleteByQuery(server, query, pagePath);
                    }
                }
            }
        } catch(Exception e){
            log.error("Error while treating events",e);
        }
    }

    public ResourceResolver getResourceResolver() throws LoginException {
        final Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, SolrSearchConstants.TEST_USER);
        if (resolverFactory != null) {
            return resolverFactory.getServiceResourceResolver(param);
        }
        return null;
    }

    private void deleteByQuery(HttpSolrClient server, SolrQuery query, String pagePath) {
        try {
            QueryResponse solrResponse = server.query(query);
            SolrDocumentList solrDocumentList = solrResponse.getResults();
            if (solrDocumentList.getNumFound() > 0) {
                server.deleteByQuery(SolrSearchConstants.SOLRDOC_FIELD_ID+":\"" + pagePath + "\"");
                server.commit();
                server.close();
            }
        } catch (SolrServerException se) {
            log.error("Error while accessing server for deletion of index", se);
        } catch (IOException ioe) {
            log.error("Error while accessing server for deletion of index", ioe);
        }
    }

    private void updateIndex(HttpSolrClient server, SolrQuery query, Resource res) {
        try {
            QueryResponse solrResponse = server.query(query);
            SolrDocumentList solrDocumentList = solrResponse.getResults();
            //if (solrDocumentList.getNumFound() > 0) {
            Resource resource = res.adaptTo(Page.class).getContentResource();
            JSONObject dataObject = solrSearchService.createPageMetadataObject(resource);
            boolean resultindexingPages = solrSearchService
                    .indexPageToSolr(dataObject, server);
            if (resultindexingPages) {
                log.info("Indexed the resource at - " + res.getParent());
            }
            // }
        }catch (SolrServerException se) {
            log.error("Error while accessing server for updation of index", se);
        } catch (IOException ioe) {
            log.error("Error while accessing server for updation of index", ioe);
        }catch (JSONException json) {
            log.error("Error while accessing server for updation of index", json);
        }
    }
}
