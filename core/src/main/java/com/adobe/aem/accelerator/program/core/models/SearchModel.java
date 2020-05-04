package com.adobe.aem.accelerator.program.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class SearchModel {

	@Inject
	@Default(values = "/content/we-retail/language-masters/en")
	@Optional
	private String searchRootPath;

	@Inject
	@Optional
	private String assetOrPage;

	@Inject
	@Optional
	private String[] tagsList;

	@Inject
	@Optional
	private String hits;

	public String getSearchRootPath() {
		return searchRootPath;
	}

	public void setSearchRootPath(String searchRootPath) {
		this.searchRootPath = searchRootPath;
	}

	public String getAssetOrPage() {
		return assetOrPage;
	}

	public void setAssetOrPage(String assetOrPage) {
		this.assetOrPage = assetOrPage;
	}

	public String[] getTagsList() {
		return tagsList;
	}

	public void setTagsList(String[] tagsList) {
		this.tagsList = tagsList;
	}

	public String getHits() {
		return hits;
	}

	public void setHits(String hits) {
		this.hits = hits;
	}

}
