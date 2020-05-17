package com.adobe.aem.accelerator.program.core.elastic.indexing.contentbuilder;

import com.adobe.aem.accelerator.program.core.elastic.indexing.DocumentModel;
import com.adobe.aem.accelerator.program.core.elastic.indexing.config.ElasticSearchIndexConfiguration;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Calendar;

@Component(service= ElasticSearchContentBuilder.class, immediate = true)
public class PageContentBuilder extends ElasticSearchContentBuilderImpl {

  private static final Logger LOG = LoggerFactory.getLogger(PageContentBuilder.class);
  public static final String PRIMARY_TYPE_VALUE = "cq:Page";

  private static final String[] FIXED_RULES = {"cq:template", "jcr:description", "jcr:title","cq:tags"};

  @Override
  public DocumentModel create(String path, @Nonnull ResourceResolver resolver,ElasticSearchIndexConfiguration elasticSearchIndexConfiguration) {
    String[] indexRules = getIndexRules(PRIMARY_TYPE_VALUE);
    if (ArrayUtils.isNotEmpty(indexRules)) {
      PageManager pageManager = resolver.adaptTo(PageManager.class);
      if (pageManager != null) {
        Page page = pageManager.getPage(path);
        if (page != null) {

          DocumentModel ret = new DocumentModel(elasticSearchIndexConfiguration.getIndex(), "page", path);
          Resource res = page.getContentResource();
          if (res != null) {
            ret.addContent(getProperties(res, indexRules));
            ret.addContent("type","page");
            if(res.getValueMap().get("cq:lastModified")!=null){
              ret.addContent("cq:lastModified",res.getValueMap().get("cq:lastModified", Calendar.class).getTimeInMillis());
            }
          }
          return ret;
        }
      }
    }
    else {
      LOG.warn("ERROR ADAPTING TO ASSET " + PRIMARY_TYPE_VALUE);
    }
    return null;
  }

  @Override
  protected String[] getFixedRules() {
    return FIXED_RULES;
  }

}
