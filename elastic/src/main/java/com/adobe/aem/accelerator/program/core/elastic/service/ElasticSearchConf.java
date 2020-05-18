package com.adobe.aem.accelerator.program.core.elastic.service;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Elastic Search Provider Configuration")
public @interface ElasticSearchConf {

    @AttributeDefinition(name = "host",
            description = "Hostname of the ElasticSearch")
    public String getHost() default "localhost";

    @AttributeDefinition(name = "port",
            description = "Port of the ElasticSearch (default: 9200)."
    )
    String getPort() default "9200";

    @AttributeDefinition(name = "protocol",
            description = "Port of the ElasticSearch (default: 9200)."
    )
    String getProtocol() default "http";
}
