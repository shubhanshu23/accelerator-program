
package com.adobe.aem.accelerator.program.core.elastic.service;

import lombok.Getter;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component(service= ElasticSearchClientService.class)
public class ElasticSearchClientService {

  private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchHostConfiguration.class);

  @Reference
  ElasticSearchHostConfiguration hostConfiguration;

  @Getter
  private RestClient restClient;

  private RestHighLevelClient restHighLevelClient;

  @Activate
  public void activate(ComponentContext context) {
    restClient = RestClient.builder(new HttpHost(hostConfiguration.getHost(), hostConfiguration.getPort(), hostConfiguration.getProtocol())).build();
    restHighLevelClient = new RestHighLevelClient(
            RestClient.builder(new HttpHost(hostConfiguration.getHost(), hostConfiguration.getPort(), hostConfiguration.getProtocol())));
  }

  @Deactivate
  public void deactivate(ComponentContext context) {
    if (this.restClient != null) {
      try {
        this.restClient.close();
      }
      catch (IOException ioe) {
        LOG.warn("Could not close ElasticSearch RestClient", ioe);
      }
    }
    if (this.restHighLevelClient != null) {
      try {
        this.restHighLevelClient.close();
      }
      catch (IOException ioe) {
        LOG.warn("Could not close ElasticSearch RestHighLevelClient", ioe);
      }
    }
  }

  public ElasticSearchHostConfiguration getHostConfiguration() {
    return hostConfiguration;
  }

  public RestClient getRestClient() {
    return restClient;
  }

  public RestHighLevelClient getRestHighLevelClient() {
    return restHighLevelClient;
  }
}
