package com.adobe.aem.accelerator.program.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = {SlingHttpServletRequest.class})
public class LinkHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(LinkHelper.class);
  
  private static final String HTML = ".html";
  
  @Inject
  @Optional
  @Named("link")
  @Source("request-attributes")
  private String link;
  
  private String linkFormatted = "";
  
  private String fullUrl = "";
  
  private boolean isExternal = false;
  
  @PostConstruct
  public void init() {
    this.isExternal = false;
    LOGGER.debug("link : {}", this.link);
    if (this.link != null) {
      if (isInternal(this.link) && !this.link.endsWith(".html")) {
        this.linkFormatted = this.link.concat(".html");
        this.fullUrl = this.link.concat(".html");
      } else {
        this.linkFormatted = this.link;
        if (this.link.indexOf("?") == -1) {
          this.fullUrl = this.link;
        } else {
          this.fullUrl = this.link.substring(0, this.link.indexOf("?"));
        } 
        this.isExternal = true;
      } 
      LOGGER.debug("linkFormatted : {}", this.linkFormatted);
      LOGGER.debug("fullUrl : {}", this.fullUrl);
    } 
  }
  
  private boolean isInternal(String linkStr) {
    return (linkStr.startsWith("/content/") && !linkStr.startsWith("/content/dam") && !linkStr.contains("?"));
  }
  
  public String getLinkFormatted() {
    return this.linkFormatted;
  }
  
  public String getFullUrl() {
    return this.fullUrl;
  }
  
  public boolean isExternal() {
    return this.isExternal;
  }
}
