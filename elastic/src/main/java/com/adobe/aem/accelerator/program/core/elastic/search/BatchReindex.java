package com.adobe.aem.accelerator.program.core.elastic.search;

import com.adobe.aem.accelerator.program.core.elastic.indexing.DocumentModel;
import com.adobe.aem.accelerator.program.core.elastic.indexing.config.ElasticSearchIndexConfiguration;
import com.adobe.aem.accelerator.program.core.elastic.indexing.contentbuilder.AssetContentBuilder;
import com.adobe.aem.accelerator.program.core.elastic.indexing.contentbuilder.PageContentBuilder;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = BatchReindex.class)
public class BatchReindex {

    private static final Logger LOG = LoggerFactory.getLogger(BatchReindex.class);
    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private SlingRepository repository;

    @Reference
    ElasticSearchIndexConfiguration elasticSearchIndexConfiguration;

    public List<DocumentModel> crawlContent(String resourceType, String resourcePath, ResourceResolver resolver) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("path", resourcePath);
        params.put("type", resourceType);
        params.put("p.offset", "0");
        params.put("p.limit", "10000");
        Session session = null;

        try {
            session = resolver.adaptTo(Session.class);
            Query query = queryBuilder.createQuery(
                    PredicateGroup.create(params), session);
            SearchResult searchResults = query.getResult();
            LOG.error("Found '{}' matches for query",
                    searchResults.getTotalMatches());
            return createIndexEntry(searchResults,resolver,resourceType);
        } catch (RepositoryException e) {
            LOG.error("Exception due to", e);
        }

        return null;
    }

    public List<DocumentModel> createIndexEntry(SearchResult results, ResourceResolver resolver, String resourceType)
            throws RepositoryException {
        List<DocumentModel> docList = new ArrayList<DocumentModel>();
        for (Hit hit : results.getHits()) {
            Resource resource = hit.getResource();
            DocumentModel doc;
            if(resourceType.equals("cq:PageContent")){
                doc = new PageContentBuilder().create(resource.getParent().getPath(),resolver,elasticSearchIndexConfiguration);
                docList.add(doc);
            } else if(resourceType.equals("dam:AssetContent")){
                doc = new AssetContentBuilder().create(resource.getParent().getPath(),resolver,elasticSearchIndexConfiguration);
                docList.add(doc);
            }
        }
        return docList;
    }


}

