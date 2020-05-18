package com.adobe.aem.accelerator.program.core.config.solr;

import com.adobe.aem.accelerator.program.core.utils.solr.SolrSearchConstants;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Solr Index Service Configuration", description = "Solr Index Listener Service Configuration")
public @interface SolrIndexListenerConfiguration {

    @AttributeDefinition(name = SolrSearchConstants.SOLR_INDEX_LISTENER_ENABLED, description = "Enable/Disable the listener")
    boolean getListenerEnabled();

    @AttributeDefinition(name = SolrSearchConstants.SOLR_IGNORE_INDEX_PAGEPATH, description = "Page Path of the pages not to be indexed, can be a root path or a single page")
    String[] getIgnoreIndexContentPath();

    @AttributeDefinition(name = SolrSearchConstants.SOLR_INDEX_PROPERTIES, description = "Ignore the properties from indexing")
    String[] getIgnoreProperties();

}