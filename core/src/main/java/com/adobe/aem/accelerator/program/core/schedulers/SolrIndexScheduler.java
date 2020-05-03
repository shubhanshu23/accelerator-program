package com.adobe.aem.accelerator.program.core.schedulers;

import com.adobe.aem.accelerator.program.core.services.solr.SolrSearchService;
import com.adobe.aem.accelerator.program.core.services.solr.SolrServerConfigurationService;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Designate(ocd=SolrIndexScheduler.Config.class)
@Component(service=Runnable.class, immediate = true)

public class SolrIndexScheduler implements Runnable {

    @ObjectClassDefinition(name="Solr Index scheduled task",
            description = "Solr Indexing - cron-job with properties")
    public static @interface Config {

        @AttributeDefinition(name = "Cron-job expression")
        String scheduler_expression() default "*/1 * * * *";

        @AttributeDefinition(name = "Concurrent task")
        boolean scheduler_concurrent() default false;
    }

    private final Logger log = LoggerFactory.getLogger(SolrIndexScheduler.class);

    @Reference
    SolrSearchService solrSearchService;

    @Reference
    SolrServerConfigurationService solrConfigurationService;

    @Override
    public void run() {
        log.info(" ****** Solr Index Scheduler ******* ");
        log.info("Solr index job execution begins");
        final String protocol = solrConfigurationService.getSolrProtocol();
        final String serverName = solrConfigurationService.getSolrServerName();
        final String serverPort = solrConfigurationService.getSolrServerPort();
        final String coreName = solrConfigurationService.getSolrCoreName();
        final String pagesResourcePath = solrConfigurationService
                .getContentPagePath();
        String URL = protocol + "://" + serverName + ":" + serverPort
                + "/solr/" + coreName;
        HttpSolrClient server = new HttpSolrClient(URL);
        try {
            JSONArray indexPageData = solrSearchService.crawlContent(
                    pagesResourcePath, "cq:PageContent");
            boolean resultindexingPages = solrSearchService
                    .indexPagesToSolr(indexPageData, server);
            if (resultindexingPages == true) {
                log.info("Successfully indexed content pages to Solr server!");
            } else {
                log.info("Error during indexing content pages to Solr server!");
            }
        } catch (IOException | JSONException | SolrServerException e) {
            log.error("Exception due to", e);
        }
    }

    @Activate
    public void activate(final Config config) {
        log.info("Solr Index Scheduler ****");

    }
}
