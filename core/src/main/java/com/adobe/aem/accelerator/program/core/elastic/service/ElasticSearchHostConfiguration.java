package com.adobe.aem.accelerator.program.core.elastic.service;

import lombok.Getter;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

/**
 * Configuration for a single ElasticSearch host
 */
@Component(service = ElasticSearchHostConfiguration.class, immediate = true, name = ElasticSearchHostConfiguration.SERVICE_NAME,
        property = "webconsole.configurationFactory.nameHint=Host: {host}")

@Designate(ocd = ElasticSearchConf.class)
public class ElasticSearchHostConfiguration {


    public static final String SERVICE_NAME = "ElasticSearch Search Provider";
    public static final String SERVICE_DESCRIPTION = "Configuration for the ElasticSearch Search Provider";


    @Getter
    protected String protocol;
    @Getter
    protected String host;
    @Getter
    protected int port;

    @Activate
    public void activate(ElasticSearchConf conf) {
        this.protocol = conf.getProtocol();
        this.host = conf.getHost();
        this.port = Integer.parseInt(conf.getPort());
    }

  public static String getServiceName() {
    return SERVICE_NAME;
  }

  public static String getServiceDescription() {
    return SERVICE_DESCRIPTION;
  }

  public String getProtocol() {
    return protocol;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }
}
