package com.adobe.aem.accelerator.program.core.services.solr.impl;

import com.adobe.aem.accelerator.program.core.config.solr.SolrIndexListenerConfiguration;
import com.adobe.aem.accelerator.program.core.services.solr.SolrIndexListenerConfigurationService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service= SolrIndexListenerConfigurationService.class, immediate = true)
@Designate(ocd = SolrIndexListenerConfiguration.class)
public class SolrIndexListenerServiceImpl implements SolrIndexListenerConfigurationService {
    private static final Logger LOG = LoggerFactory.getLogger(SolrServerConfigurationImpl.class);

    private SolrIndexListenerConfiguration config;
    private boolean isListenerEnabled;
    private String[] ignoreIndexContentPath;
    private String [] ignoreProperties;

    @Activate
    protected void activate(SolrIndexListenerConfiguration serverConfig) {
        this.config = serverConfig;
        isListenerEnabled = serverConfig.getListenerEnabled();
        ignoreIndexContentPath = serverConfig.getIgnoreIndexContentPath();
        ignoreProperties = serverConfig.getIgnoreProperties();
    }

    public boolean getListenerEnabled() {
        return isListenerEnabled;
    }

    public void setListenerEnabled(Boolean listenerEnabled) {
        isListenerEnabled = listenerEnabled;
    }

    public String[] getIgnoreIndexContentPath() {
        return ignoreIndexContentPath;
    }

    public void setIgnoreIndexContentPath(String[] ignoreIndexContentPath) {
        this.ignoreIndexContentPath = ignoreIndexContentPath;
    }

    public String[] getIgnoreProperties() {
        return ignoreProperties;
    }

    public void setIgnoreProperties(String[] ignoreProperties) {
        this.ignoreProperties = ignoreProperties;
    }
}
