package com.adobe.aem.accelerator.program.core.elastic.index;

import com.adobe.aem.accelerator.program.core.elastic.indexing.contentbuilder.AssetContentBuilder;
import com.adobe.aem.accelerator.program.core.elastic.service.ElasticSearchClientService;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component(service = IndexClient.class)
public class IndexClient {
    private static final Logger LOG = LoggerFactory.getLogger(IndexClient.class);

    @Reference
    ElasticSearchClientService elasticSearchClientService;

    public boolean createIndex(String indexName) throws IOException {
        RestHighLevelClient restHighLevelClient = elasticSearchClientService.getRestHighLevelClient();
        try {
            if (!indexExists(indexName, restHighLevelClient)) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
                createIndexRequest.source(IndexContants.DEFAULT_AEM, XContentType.JSON);
                restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                return true;
            }
        } catch (Exception e) {
           LOG.error("Exception {}",e.getMessage(),e);
        }
        return false;
    }

    public boolean indexExists(String indexName, RestHighLevelClient restHighLevelClient) throws IOException {

        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
            return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            LOG.error("Exception {}",e.getMessage(),e);
        }
        return false;
    }

}
