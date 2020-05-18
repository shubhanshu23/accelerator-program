package com.adobe.aem.accelerator.program.core.services.solr;

public interface SolrServerConfigurationService {

	public String getSolrProtocol();

	public String getSolrServerName();

	public String getSolrServerPort();

	public String getSolrCoreName();
	
	public String getContentPagePath();
	
}
