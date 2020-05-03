package com.adobe.aem.accelerator.program.core.services.solr.impl;


import com.adobe.aem.accelerator.program.core.config.solr.SolrServerConfiguration;
import com.adobe.aem.accelerator.program.core.services.solr.SolrServerConfigurationService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service= SolrServerConfigurationService.class, immediate = true)
@Designate(ocd = SolrServerConfiguration.class)
public class SolrServerConfigurationImpl implements SolrServerConfigurationService {
	private static final Logger LOG = LoggerFactory.getLogger(SolrServerConfigurationImpl.class);

	private SolrServerConfiguration config;
	private String solrProtocol;
	private String solrServerName;
	private String solrServerPort;
	private String solrCoreName;
	private String contentPagePath;

	@Activate
	protected void activate(SolrServerConfiguration serverConfig)  {
		LOG.info("inside activate method in solr configuration service ");
		this.config = serverConfig;
		solrProtocol = serverConfig.getSolrProtocol();
		solrServerName = serverConfig.getSolrServerName();
		solrServerPort = serverConfig.getSolrServerPort();
		solrCoreName = serverConfig.getSolrCoreName();
		contentPagePath = serverConfig.getContentPagePath();
	}

	public String getSolrProtocol() {
		return solrProtocol;
	}

	public String getSolrServerName() {
		return solrServerName;
	}

	public String getSolrServerPort() {
		return solrServerPort;
	}

	public String getSolrCoreName() {
		return solrCoreName;
	}
	
	public String getContentPagePath() {
		return contentPagePath;
	}

}
