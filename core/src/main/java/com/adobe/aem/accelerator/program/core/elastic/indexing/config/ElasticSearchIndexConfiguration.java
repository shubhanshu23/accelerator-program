package com.adobe.aem.accelerator.program.core.elastic.indexing.config;

import org.apache.jackrabbit.JcrConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ElasticSearchIndexConfiguration.class, immediate = true, name = ElasticSearchIndexConfiguration.SERVICE_NAME)
@Designate(ocd = ElasticConf.class)
public class ElasticSearchIndexConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchIndexConfiguration.class);

    public static final String PRIMARY_TYPE = JcrConstants.JCR_PRIMARYTYPE;

    public static final String SERVICE_NAME = "ElasticSearch Index Configuration";

    protected String[] indexAssetRules;
    protected String[] indexPageRules;
    protected String index;
    protected String contentPath;
    protected String assetPath;
    @Activate
    public void activate(ElasticConf conf) {
        this.index = conf.getIndex();
        this.indexAssetRules = conf.getIndexAssetRules();
        this.indexPageRules = conf.getIndexPageRules();
        this.contentPath = conf.getContentPath();
        this.assetPath = conf.getAssetPath();
    }

    public static Logger getLOG() {
        return LOG;
    }

    public static String getPrimaryType() {
        return PRIMARY_TYPE;
    }

    public static String getServiceName() {
        return SERVICE_NAME;
    }

    public String[] getIndexAssetRules() {
        return indexAssetRules;
    }

    public void setIndexAssetRules(String[] indexAssetRules) {
        this.indexAssetRules = indexAssetRules;
    }

    public String[] getIndexPageRules() {
        return indexPageRules;
    }

    public void setIndexPageRules(String[] indexPageRules) {
        this.indexPageRules = indexPageRules;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }
}
