package com.adobe.aem.accelerator.program.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.aem.accelerator.program.core.models.SearchModel;
import com.adobe.aem.accelerator.program.core.models.SearchResultKeys;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

/*
 * author: sraghav
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Query Buider Demo Servlet",
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
		ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "="
				+ "accelerator-program/components/content/searchcomponent",
		ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=" + "json" })
public class QueryBuilderServlet extends SlingAllMethodsServlet {

	private static final String KEYWORD = "keyword";
	private static final long serialVersionUID = 1L;
	private static final String PAGE = "page";
	private static final String DAM_ASSET = "dam:Asset";
	private static final String CQ_PAGE = "cq:Page";

	@Reference
	private QueryBuilder queryBuilder;

	private Session session;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		/**
		 * This parameter is passed in the HTTP call
		 */
		String keyword = request.getParameter(KEYWORD);

		response.setContentType("application/json");

		/**
		 * Get resource resolver instance
		 */
		ResourceResolver resourceResolver = request.getResourceResolver();

		try {
			if (resourceResolver != null) {
				Resource resource = resourceResolver.getResource(
						"/content/accelerator-program/language-masters/en/jcr:content/root/responsivegrid/searchcomponent");
				/**
				 * Adapt resource to Search Model and read dialog values
				 */
				SearchModel model = null;
				if (resource != null) {
					model = resource.adaptTo(SearchModel.class);
				}
				/**
				 * Adapting the resource resolver to the session object
				 */
				session = resourceResolver.adaptTo(Session.class); // creating session

				/**
				 * Map for the predicates
				 */
				Map<String, String> predicate = new HashMap<>();

				/**
				 * Configuring the Map for the predicate
				 */
				predicate.put("path", model.getSearchRootPath());
				predicate.put("fulltext", keyword);

				/**
				 * Author can choose page/asset/all from the dialog
				 */
				if (model.getAssetOrPage().equals(PAGE)) {
					predicate.put("type", CQ_PAGE);
					if (model.getTagsList() != null) {
						for (int i = 0; i < model.getTagsList().length; i++) {
							predicate.put("group.p.or", "true"); // combine this group with OR
							predicate.put("group." + String.valueOf(i) + "_property", "jcr:content/cq:tags");
							predicate.put("group." + String.valueOf(i) + "_property.value", model.getTagsList()[i]);
						}
					}
				} else if (model.getAssetOrPage().equals("damasset")) {
					predicate.put("type", DAM_ASSET);
					if (model.getTagsList() != null) {
						for (int i = 0; i < model.getTagsList().length; i++) {
							predicate.put("group.p.or", "true"); // combine this group with OR
							predicate.put("group." + String.valueOf(i) + "_property", "jcr:content/metadata/cq:tags");
							predicate.put("group." + String.valueOf(i) + "_property.value", model.getTagsList()[i]);
						}
					}
				} else {
					if (model.getTagsList() != null) {
						for (int i = 0; i < model.getTagsList().length; i++) {
							predicate.put("group.p.or", "true"); // combine this group with OR
							predicate.put("group." + String.valueOf(i) + "_property", "cq:tags");
							predicate.put("group." + String.valueOf(i) + "_property.value", model.getTagsList()[i]);
						}
					}
				}

				/**
				 * Creating the Query instance
				 */
				Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), session);

				/**
				 * Setting offset and limit can be done using predicates or with Query methods
				 */
				query.setStart(0); // same as predicate.put("p.offset", "0");
				query.setHitsPerPage(Integer.valueOf(model.getHits())); // same as predicate.put("p.limit", "60");

				/**
				 * Getting the search results
				 */
				SearchResult searchResult = query.getResult();
				Gson gson = new GsonBuilder().create();
				List<SearchResultKeys> keys = new ArrayList<SearchResultKeys>();
				int i = 0;
				// iterating over the results
				for (Hit hit : searchResult.getHits()) {
					SearchResultKeys key = new SearchResultKeys();
					// Json data has index path
					key.setIndex(String.valueOf(++i));
					key.setPath(hit.getPath());

					// Json data has page title and page description - if page is selected
					// Json data has asset title and asset description - if asset is selected
					if (model.getAssetOrPage().equals(PAGE)) {
						Resource res = resourceResolver.getResource(hit.getPath());
						if (res != null) {
							Page page = res.adaptTo(Page.class);
							key.setPageTitle(page.getTitle());
							key.setPageDescription(page.getDescription());
						}

					} else if (model.getAssetOrPage().equals(DAM_ASSET)) {
						Resource res = resourceResolver.getResource(hit.getPath());
						if (res != null) {
							Resource jcrContent = res.getChild(hit.getPath());
							Resource metadada = jcrContent.getChild(jcrContent.getPath());
							key.setAssetTitle(metadada.getValueMap().get("dc:title", "NA"));
							key.setAssetTitle(metadada.getValueMap().get("dc:description", "NA"));
						}
					}
					keys.add(key);
				}
				JsonArray jsonData = gson.toJsonTree(keys).getAsJsonArray();
				response.getWriter().println(jsonData);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (resourceResolver != null) {
				resourceResolver.close();
			}
			if (session != null) {
				session.logout(); // logging off session object
			}
		}

	}

}
