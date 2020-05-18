package com.adobe.aem.accelerator.program.core.config.solr;

import com.adobe.aem.accelerator.program.core.utils.solr.SolrSearchConstants;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Solr Service Configuration", description = "Solr Service Configuration")
public @interface SolrServerConfiguration {

    @AttributeDefinition(name = SolrSearchConstants.SOLR_PROTOCOL, description = "Either 'http' or 'https'")
    String getSolrProtocol();

    @AttributeDefinition(name = SolrSearchConstants.SOLR_SERVER_NAME, description = "Server name or IP address ")
    String getSolrServerName();

    @AttributeDefinition(name = SolrSearchConstants.SOLR_SERVER_PORT, description = "Server port")
    String getSolrServerPort();

    @AttributeDefinition(name = SolrSearchConstants.SOLR_CORE_NAME, description = "Core name in solr server")
    String getSolrCoreName();

    @AttributeDefinition(name = SolrSearchConstants.SOLR_PAGEPATH, description = "Content Page Path")
    String getContentPagePath();

}
