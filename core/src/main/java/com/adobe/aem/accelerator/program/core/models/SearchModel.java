package com.adobe.aem.accelerator.program.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Model(adaptables = Resource.class)
public class SearchModel {

	private static final String PAGE = "page";

	private static final String DAM_ASSET = "dam:Asset";

	private static final String CQ_PAGE = "cq:Page";

	private static final String KEYWORD = "keyword";

	@Inject
	@Default(values = "/content/restwithaemcontent/en")
	@Optional
	private String searchRootPath;

	@Inject
	@Optional
	private String assetOrPage;

	@Inject
	@Optional
	private String keywordOrTag;

	@Inject
	@Optional
	private String tagsProperty;

	@Inject
	@Optional
	private String[] tagsList;

	@Inject
	@Optional
	private String searchText;

	@Inject
	@Optional
	private String orOrAnd;

	private ArrayList<String> resultPaths;

	@Inject
	@Optional
	private ResourceResolver resourceResolver;

	private Session session;

	@Inject
	private QueryBuilder queryBuilder;

	@PostConstruct
	public void init() {
		resultPaths = getResultPath();
	}

	private ArrayList<String> getResultPath() {
		ArrayList<String> path = new ArrayList<String>();

		try {
			session = resourceResolver.adaptTo(Session.class);

			Map<String, String> predicate = new HashMap<>();
			predicate.put("path", searchRootPath);

			if (keywordOrTag.equals(KEYWORD)) {
				predicate.put("fulltext", searchText);
				if (assetOrPage.equals(PAGE)) {
					predicate.put("type", CQ_PAGE);
				} else if (assetOrPage.equals("damasset")) {
					predicate.put("type", DAM_ASSET);
				}

			} else {
				if (assetOrPage.equals(PAGE)) {
					predicate.put("type", CQ_PAGE);
				} else if (assetOrPage.equals("damasset")) {
					predicate.put("type", DAM_ASSET);
				}
				for (int i = 0; i < tagsList.length; i++) {
					predicate.put("group.p.or", orOrAnd);
					predicate.put("group." + String.valueOf(i) + "_property", tagsProperty);
					predicate.put("group." + String.valueOf(i) + "_property.value", tagsList[i]);
				}
			}

			Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), session);
			query.setStart(0);
			query.setHitsPerPage(80);

			SearchResult searchResult = query.getResult();
			for (Hit hit : searchResult.getHits()) {
				path.add(hit.getPath());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} /*
			 * finally { if (session != null) { session.logout(); } }
			 */

		return path;
	}

	public String getSearchRootPath() {
		return searchRootPath;
	}

	public void setSearchRootPath(String searchRootPath) {
		this.searchRootPath = searchRootPath;
	}

	public String getKeywordOrTag() {
		return keywordOrTag;
	}

	public void setKeywordOrTag(String keywordOrTag) {
		this.keywordOrTag = keywordOrTag;
	}

	public String getAssetOrPage() {
		return assetOrPage;
	}

	public void setAssetOrPage(String assetOrPage) {
		this.assetOrPage = assetOrPage;
	}

	public ArrayList<String> getResultPaths() {
		return resultPaths;
	}

	public void setResultPaths(ArrayList<String> resultPaths) {
		this.resultPaths = resultPaths;
	}

	public String getTagsProperty() {
		return tagsProperty;
	}

	public void setTagsProperty(String tagsProperty) {
		this.tagsProperty = tagsProperty;
	}

	public String[] getTagsList() {
		return tagsList;
	}

	public void setTagsList(String[] tagsList) {
		this.tagsList = tagsList;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public String getOrOrAnd() {
		return orOrAnd;
	}

	public void setOrOrAnd(String orOrAnd) {
		this.orOrAnd = orOrAnd;
	}

}
