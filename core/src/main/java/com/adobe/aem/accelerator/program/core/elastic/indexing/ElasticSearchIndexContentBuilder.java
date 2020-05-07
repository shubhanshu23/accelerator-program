package com.adobe.aem.accelerator.program.core.elastic.indexing;

import com.adobe.aem.accelerator.program.core.elastic.indexing.contentbuilder.AssetContentBuilder;
import com.adobe.aem.accelerator.program.core.elastic.indexing.contentbuilder.ElasticSearchContentBuilder;
import com.adobe.aem.accelerator.program.core.elastic.indexing.contentbuilder.PageContentBuilder;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component(service = ContentBuilder.class, property = {"name=elastic", Constants.SERVICE_RANKING + ":Integer=1001"})
public class ElasticSearchIndexContentBuilder implements ContentBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchIndexContentBuilder.class);
    private BundleContext context;

    @Reference
    private ResourceResolverFactory resolverFactory;

    /**
     * Name of the Content Builder
     */
    public static final String NAME = "elastic";
    /**
     * Title of the Content Builder
     */
    public static final String TITLE = "Elastic Search Index Content";

    @Activate
    public void activate(BundleContext context) {
        this.context = context;
        LOG.info("context" + context);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public ReplicationContent create(Session session, ReplicationAction action, ReplicationContentFactory factory) throws ReplicationException {
        return create(session, action, factory, null);
    }

    @Override
    public ReplicationContent create(Session session, ReplicationAction action, ReplicationContentFactory factory, Map<String, Object> map) throws ReplicationException {
        String path = action.getPath();
        ReplicationLog log = action.getLog();
        log.info("Path :" + path);
        if (StringUtils.isNotBlank(path)) {
            try {
                HashMap<String, Object> sessionMap = new HashMap<>();
                sessionMap.put(JcrResourceConstants.AUTHENTICATION_INFO_SESSION, session);
                ResourceResolver resolver = resolverFactory.getResourceResolver(sessionMap);

                Resource resource = resolver.getResource(path);
                if (resource != null) {
                    String primaryType = resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, String.class);
                    ElasticSearchContentBuilder builder = getContentBuilder(primaryType, log);
                    if (builder != null) {
                        ReplicationContent replicationContent = createReplicationContent(factory, builder.create(path, resolver));
                        log.info("Replication content :"+replicationContent);
                        return replicationContent;
                    }
                }
            } catch (LoginException e) {
                log.error("Page Manager Exception", e);
            }

        } else {
            log.info(getClass().getSimpleName() + ": Path is blank");
        }
        return ReplicationContent.VOID;
    }

    /**
     * Looks up a ContentBuilder implementation for the given PrimaryType.
     *
     * @param primaryType
     * @param log
     * @return ElasticSearchIndexConfiguration or null if none found
     */
    private ElasticSearchContentBuilder getContentBuilder(String primaryType, ReplicationLog log) {
        ElasticSearchContentBuilder elasticSearchContentBuilder = null;
        if (primaryType.equals("dam:Asset")) {
            elasticSearchContentBuilder = new AssetContentBuilder();
        } else if (primaryType.equals("cq:Page")) {
            elasticSearchContentBuilder = new PageContentBuilder();
        }
        return elasticSearchContentBuilder;
    }

    private ReplicationContent createReplicationContent(ReplicationContentFactory factory, DocumentModel content) throws ReplicationException {
        Path tempFile;
        try {
            tempFile = Files.createTempFile("elastic_index", ".tmp");
        } catch (IOException e) {
            throw new ReplicationException("Failed to create a temp file", e);
        }

        return getReplicationContent(factory, content, tempFile);
    }

    private ReplicationContent getReplicationContent(ReplicationContentFactory factory, DocumentModel content, Path tempFile) throws ReplicationException {
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, Charset.forName("UTF-8"))) {
            ObjectMapper mapper = new ObjectMapper();
            writer.write(mapper.writeValueAsString(content));
            writer.flush();

            return factory.create("text/plain", tempFile.toFile(), true);
        } catch (IOException e) {
            throw new ReplicationException("Could not write to temporary file", e);
        }
    }

}
