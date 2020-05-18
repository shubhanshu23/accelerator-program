package com.adobe.aem.accelerator.program.core.elastic.search;

import com.adobe.aem.accelerator.program.core.elastic.index.IndexClient;
import com.adobe.aem.accelerator.program.core.elastic.indexing.DocumentModel;
import com.adobe.aem.accelerator.program.core.elastic.indexing.config.ElasticSearchIndexConfiguration;
import com.adobe.aem.accelerator.program.core.elastic.service.ElasticSearchClientService;
import com.adobe.aem.accelerator.program.core.elastic.service.ElasticSearchHostConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.List;

@Component(service = Servlet.class, immediate = true, enabled = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "= Bulk ReIndex Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/bulkindex"})
public class BulkIndexServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(BulkIndexServlet.class);

    @Reference
    BatchReindex batchReindex;

    @Reference
    ElasticSearchIndexConfiguration elasticSearchIndexConfiguration;

    @Reference
    ElasticSearchClientService elasticSearchClientService;

    @Reference
    ElasticSearchHostConfiguration hostConfiguration;

    @Reference
    IndexClient client;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        ResourceResolver resourceResolver = request.getResourceResolver();
        try {
            String pageindex = request.getParameter("pageindex");
            String assetindex = request.getParameter("assetindex");
            RestHighLevelClient restHighLevelClient = elasticSearchClientService.getRestHighLevelClient();
            if(!client.indexExists(elasticSearchIndexConfiguration.getIndex(),restHighLevelClient)){
                client.createIndex(elasticSearchIndexConfiguration.getIndex());
            }

            BulkRequest bulkRequest = new BulkRequest();
            if(pageindex.equals("true")){
                List<DocumentModel> docList = batchReindex.crawlContent("cq:PageContent",
                        elasticSearchIndexConfiguration.getContentPath(), resourceResolver);
                bulkInsert(bulkRequest,docList);
            }
            if(assetindex.equals("true")){
                List<DocumentModel> assetList = batchReindex.crawlContent("dam:AssetContent",
                        elasticSearchIndexConfiguration.getAssetPath(),resourceResolver);
                bulkInsert(bulkRequest,assetList);
            }
            BulkResponse bulkResponse = getBulkItemResponses(bulkRequest,restHighLevelClient);
            response.setContentType("text/plain");
            LOG.info("BulkResponse {}",bulkResponse.status());
            response.getOutputStream().print("Bulk Update Success ::"+!bulkResponse.hasFailures());
        } catch (Exception e) {
            LOG.error("Exception {}", e.getMessage(), e);
        }finally {
            if(resourceResolver.isLive())
                resourceResolver.close();
        }
    }


    public BulkRequest bulkInsert(BulkRequest bulkRequest,List<DocumentModel> docList) {
        LOG.error("docList length {}", docList.size());
        ObjectMapper objectMapper = new ObjectMapper();
        docList.forEach(doc -> {
            IndexRequest indexRequest = null;
            try {
                LOG.info("doc. path"+doc.getPath());
                if(doc.getId()!=null && doc.getContent()!=null){
                    indexRequest = new IndexRequest(elasticSearchIndexConfiguration.getIndex()).id(doc.getId()).source(objectMapper.writeValueAsString(doc.getContent()), XContentType.JSON);
                    bulkRequest.add(indexRequest);
                }

            } catch (Exception e) {
                LOG.error("Exception {}",e.getMessage(),e);
            }

        });
        return bulkRequest;
    }

    private BulkResponse getBulkItemResponses(BulkRequest bulkRequest,RestHighLevelClient restHighLevelClient) {
        BulkResponse bulkResponse=null;
        try {
            bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            LOG.info("BulkResponse {}", bulkResponse);
        } catch (Exception e) {
            LOG.error("Exception {}", e.getMessage(), e);
        }
        return bulkResponse;
    }
}
