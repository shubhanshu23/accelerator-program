package com.adobe.aem.accelerator.program.core.services.solr;

public interface SolrIndexListenerConfigurationService {

     boolean getListenerEnabled();

    String[] getIgnoreIndexContentPath();

    String[] getIgnoreProperties();
}
