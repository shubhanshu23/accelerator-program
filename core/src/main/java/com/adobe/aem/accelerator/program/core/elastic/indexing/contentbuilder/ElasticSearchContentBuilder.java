package com.adobe.aem.accelerator.program.core.elastic.indexing.contentbuilder;

import com.adobe.aem.accelerator.program.core.elastic.indexing.DocumentModel;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;

public interface ElasticSearchContentBuilder {

  public DocumentModel create(String path, @Nonnull ResourceResolver resolver);

}
