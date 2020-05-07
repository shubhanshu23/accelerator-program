package com.adobe.aem.accelerator.program.core.elastic.indexing.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Elastic Search Index Configuration")
public @interface ElasticConf {

    @AttributeDefinition(
            name = "indexRules for Page",
            cardinality = Integer.MAX_VALUE,
            description = "List with the names of all properties that should be indexed."
    )
    public String[] getIndexPageRules();

    @AttributeDefinition(
            name = "indexRules for Asset",
            cardinality = Integer.MAX_VALUE,
            description = "List with the names of all properties that should be indexed."
    )
    public String[] getIndexAssetRules();

    @AttributeDefinition(
            name = "indexName",
            cardinality = Integer.MAX_VALUE,
            description = "name of the index in elastic search."
    )
    public String getIndex();
}
