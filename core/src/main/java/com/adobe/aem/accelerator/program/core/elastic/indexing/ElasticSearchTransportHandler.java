package com.adobe.aem.accelerator.program.core.elastic.indexing;

import com.adobe.aem.accelerator.program.core.elastic.service.ElasticSearchClientService;
import com.day.cq.replication.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.sling.commons.json.JSONException;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component(service = TransportHandler.class, immediate = true, name = "Elastic Search Index Agent",
        property = Constants.SERVICE_RANKING + "=" + 1000)
public class ElasticSearchTransportHandler implements TransportHandler {

    @Reference
    protected ElasticSearchClientService elasticSearchClientService;

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchTransportHandler.class);

    /**
     * @param config
     * @return only accept if the serializationType is "elastic"
     */
    @Override
    public boolean canHandle(AgentConfig config) {
        LOG.error("config {}", config);
        return StringUtils.equalsIgnoreCase(config.getSerializationType(), ElasticSearchIndexContentBuilder.NAME);
    }

    /**
     * @param ctx
     * @param tx
     * @return
     * @throws ReplicationException
     */
    @Override
    public ReplicationResult deliver(TransportContext ctx, ReplicationTransaction tx) throws ReplicationException {
        ReplicationLog log = tx.getLog();
        try {
            RestClient restClient = elasticSearchClientService.getRestClient();
            ReplicationActionType replicationType = tx.getAction().getType();
            LOG.error("Replication action type {}", replicationType.getName());
            if (replicationType == ReplicationActionType.TEST) {
                return doTest(ctx, tx, restClient);
            } else {
                log.info(getClass().getSimpleName() + ": >>>>>>>>>>>>>>>>>");
                if (tx.getContent() == ReplicationContent.VOID) {
                    log.info("replication content not provided");
                    return new ReplicationResult(true, 0, "No Replication Content provided for path " + tx.getAction().getPath());
                }
                switch (replicationType) {
                    case ACTIVATE:
                        return doActivate(ctx, tx, restClient);
                    case DEACTIVATE:
                        return doDeactivate(ctx, tx, restClient);
                    default:
                        log.warn(getClass().getSimpleName() + ": Replication action type" + replicationType + " not supported.");
                        throw new ReplicationException("Replication action type " + replicationType + " not supported.");
                }
            }
        } catch (JSONException e) {
            return new ReplicationResult(false, 0, e.getLocalizedMessage());
        } catch (IOException e) {
            log.error(getClass().getSimpleName() + ": unable to perform Indexing due to " + e.getLocalizedMessage());
            return new ReplicationResult(false, 0, e.getLocalizedMessage());
        }
    }

    private ReplicationResult doDeactivate(TransportContext ctx, ReplicationTransaction tx, RestClient restClient) throws IOException {
        ReplicationLog log = tx.getLog();

        ObjectMapper mapper = new ObjectMapper();
        DocumentModel content = mapper.readValue(tx.getContent().getInputStream(), DocumentModel.class);

        Request request = new Request("DELETE", "/" + content.getIndex() + "/" + "_doc" + "/" + DigestUtils.md5Hex(content.getPath()));
        Response deleteResponse = restClient.performRequest(request);
        LOG.debug(deleteResponse.toString());
        log.info(getClass().getSimpleName() + ": Delete Call returned " + deleteResponse.getStatusLine().getStatusCode() + ": " + deleteResponse.getStatusLine().getReasonPhrase());
        if (deleteResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED || deleteResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return ReplicationResult.OK;
        }
        LOG.error("Could not delete " + content.getType() + " at " + content.getPath());
        return new ReplicationResult(false, 0, "Replication failed");
    }


    private ReplicationResult doActivate(TransportContext ctx, ReplicationTransaction tx, RestClient restClient) throws ReplicationException, JSONException, IOException {
        ReplicationLog log = tx.getLog();
        ObjectMapper mapper = new ObjectMapper();
        DocumentModel content = mapper.readValue(tx.getContent().getInputStream(), DocumentModel.class);
        log.info("content>>>" + content);

        if (content != null) {
            log.info(getClass().getSimpleName() + ": Indexing " + content.getPath());
            String contentString = mapper.writeValueAsString(content.getContent());
            log.debug(getClass().getSimpleName() + ": Index-Content: " + contentString);
            LOG.debug("Index-Content: " + contentString);

            HttpEntity entity = new NStringEntity(contentString, ContentType.APPLICATION_JSON);

            Request request = new Request("PUT", "/" + content.getIndex() + "/_doc/" + DigestUtils.md5Hex(content.getPath()));
            request.setEntity(entity);
            Response indexResponse = restClient.performRequest(request);
            LOG.debug(indexResponse.toString());
            log.info(getClass().getSimpleName() + ": " + indexResponse.getStatusLine().getStatusCode() + ": " + indexResponse.getStatusLine().getReasonPhrase());
            if (indexResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED || indexResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return ReplicationResult.OK;
            }
        }
        LOG.error("Could not replicate");
        return new ReplicationResult(false, 0, "Replication failed");
    }

    private ReplicationResult doTest(TransportContext ctx, ReplicationTransaction tx, RestClient restClient) throws ReplicationException, IOException {
        ReplicationLog log = tx.getLog();
        Request request = new Request("GET", "/");
        Response response = restClient.performRequest(request);
        log.info(getClass().getSimpleName() + ": ---------------------------------------");
        log.info(getClass().getSimpleName() + ": " + response.toString());
        log.info(getClass().getSimpleName() + ": " + EntityUtils.toString(response.getEntity()));
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return ReplicationResult.OK;
        }
        return new ReplicationResult(false, 0, "Replication test failed");
    }

}
