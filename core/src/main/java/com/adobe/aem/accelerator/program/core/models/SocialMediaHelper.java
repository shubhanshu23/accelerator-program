package com.adobe.aem.accelerator.program.core.models;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class)
public class SocialMediaHelper {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Inject
    @Optional
    public Resource medialinks;
	
	@Inject
    @Optional
    public Resource	socialmedia;
	
	//LOG.info("Multifield Helper resource {}", medialinks);

}
