package com.adobe.aem.accelerator.program.core.elastic.indexing.contentbuilder;

import com.adobe.aem.accelerator.program.core.elastic.indexing.DocumentModel;
import com.adobe.aem.accelerator.program.core.elastic.indexing.config.ElasticSearchIndexConfiguration;
import com.day.cq.dam.api.Asset;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * Content Builder for DAM Assets
 */
@Component(service = ElasticSearchContentBuilder.class, immediate = true)
public class AssetContentBuilder extends ElasticSearchContentBuilderImpl {

    private static final Logger LOG = LoggerFactory.getLogger(AssetContentBuilder.class);
    public static final String PRIMARY_TYPE_VALUE = "dam:Asset";

    private static final String[] FIXED_RULES = {"dc:title", "dc:description"};

    @Override
    public DocumentModel create(String path, @Nonnull ResourceResolver resolver,ElasticSearchIndexConfiguration elasticSearchIndexConfiguration) {
        String[] indexRules = getIndexRules(PRIMARY_TYPE_VALUE);
        if (ArrayUtils.isNotEmpty(indexRules)) {
            Resource res = resolver.getResource(path);
            if (res != null) {
                Asset asset = res.adaptTo(Asset.class);
                LOG.info("res value map {}", res.getValueMap());
                if (asset != null) {
                    DocumentModel ret = new DocumentModel(elasticSearchIndexConfiguration.getIndex(), "asset", path);
                    ret.addContent(getProperties(res, indexRules));
                    ret.addContent("jcr:lastModified", asset.getLastModified());
                    ret.addContent("type", "asset");
                    return ret;
                }
                LOG.error("ERROR ADAPTING TO ASSET");
            }
        }
        return null;
    }

    @Override
    protected String[] getFixedRules() {
        return FIXED_RULES;
    }
}
