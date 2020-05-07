package com.adobe.aem.accelerator.program.core.elastic.search;

import com.adobe.aem.accelerator.program.core.elastic.indexing.DocumentModel;
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

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        ResourceResolver resourceResolver = request.getResourceResolver();
        try {
            String pageindex = request.getParameter("pageindex");
            String assetindex = request.getParameter("assetindex");
            BulkRequest bulkRequest = new BulkRequest();
            if(pageindex.equals("true")){
                List<DocumentModel> docList = batchReindex.crawlContent("cq:PageContent", "/content", resourceResolver);
                bulkInsert(bulkRequest,docList);
            }
            if(assetindex.equals("true")){
                List<DocumentModel> assetList = batchReindex.crawlContent("dam:AssetContent","/content/dam",resourceResolver);
                bulkInsert(bulkRequest,assetList);
            }
            BulkResponse bulkResponse = getBulkItemResponses(bulkRequest);
            response.setContentType("text/plain");
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
                    indexRequest = new IndexRequest("idx").id(doc.getId()).source(objectMapper.writeValueAsString(doc.getContent()), XContentType.JSON);
                    bulkRequest.add(indexRequest);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        return bulkRequest;
    }

    private BulkResponse getBulkItemResponses(BulkRequest bulkRequest) {
        BulkResponse bulkResponse=null;
        try {
            RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(
                    new HttpHost("localhost", 9200, "http")));
            bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            LOG.info("BulkResponse {}", bulkResponse);
        } catch (Exception e) {
            LOG.error("Exception {}", e.getMessage(), e);
        }
        return bulkResponse;
    }
}
