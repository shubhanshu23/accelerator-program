package com.adobe.aem.accelerator.program.core.listeners.solr;

import com.adobe.aem.accelerator.program.core.services.solr.SolrIndexListenerConfigurationService;
import com.adobe.aem.accelerator.program.core.services.solr.SolrSearchService;
import com.adobe.aem.accelerator.program.core.services.solr.SolrServerConfigurationService;
import com.adobe.aem.accelerator.program.core.utils.solr.SolrSearchConstants;
import com.adobe.aem.accelerator.program.core.utils.solr.SolrUtils;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationEvent;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


@Component(service= EventHandler.class, immediate=true,
        property = {
                EventConstants.EVENT_TOPIC +  "=" + ReplicationEvent.EVENT_TOPIC
        }
)
public class SolrIndexReplicationHandler implements EventHandler {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private SolrSearchService solrSearchService;

    @Reference
    private SolrServerConfigurationService solrServerconfig;

    @Reference
    private SolrIndexListenerConfigurationService solrListenerService;


    @Override
    public void handleEvent(Event event) {
        if(solrListenerService.getListenerEnabled()) {
            log.info("Inside handleEvent() ");
            ResourceResolver resolver = null;
            Node replicatedPageNode;
            try {
                resolver = SolrUtils.getResourceResolver(resolverFactory);
                ReplicationEvent pageEvent = ReplicationEvent.fromEvent(event);
                if (null != pageEvent) {
                    ReplicationAction replicationAction = pageEvent.getReplicationAction();
                    if (null != replicationAction) {
                        String replicatedPagePath = replicationAction.getPath();
                        String[] ignorePages = SolrUtils.getIndexDetails(resolver,"ignorePages");
                        boolean isPageIgnored =  SolrUtils.isPageIgnored(replicatedPagePath, ignorePages, resolver);
                        if (!isPageIgnored) {
                            HttpSolrClient server = solrSearchService.prepareSolrServer();
                            SolrQuery query = new SolrQuery();
                            query.set(SolrSearchConstants.SOLR_QUERY_Q,
                                    SolrSearchConstants.SOLRDOC_FIELD_ID + ":\"" + replicatedPagePath + "\"");

                            log.info("Page path '{}'", replicatedPagePath);
                            if (replicationAction.getType().equals(ReplicationActionType.ACTIVATE)) {
                                replicatedPageNode = resolver.getResource(replicationAction.getPath() + SolrSearchConstants.CONSTANT_SLASH + SolrSearchConstants.JCR_CONTENT_NODE_NAME).adaptTo(Node.class);
                                if (replicatedPageNode.isNode() && !replicatedPageNode.hasProperty(SolrSearchConstants.PROPERTY_EXCLUDE_FROM_INDEX)) {
                                    replicatedPageNode.setProperty(SolrSearchConstants.PAGE_PROPERTY_SOLR_INDEX, SolrSearchConstants.PAGE_PROPERTY_SOLR_INDEX_VALUE);
                                    replicatedPageNode.getSession().save();
                                    solrSearchService.updateIndex(server, query, resolver.getResource(replicatedPagePath));
                                }
                            } else if (replicationAction.getType().equals(ReplicationActionType.DEACTIVATE) || replicationAction.getType().equals(ReplicationActionType.DELETE)) {
                                log.info("Deactivated page '{}'", replicatedPagePath);
                                solrSearchService.deleteByQuery(server, query, replicatedPagePath);
                            }
                        }
                    }
                }
            } catch (LoginException le) {
                log.error("Login Exception :", le);
            } catch (RepositoryException re) {
                log.error("Repository Exception :", re);
            }
        }
    }

}
