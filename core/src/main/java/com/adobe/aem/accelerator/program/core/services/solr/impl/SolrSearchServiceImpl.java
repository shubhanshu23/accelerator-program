package com.adobe.aem.accelerator.program.core.services.solr.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.adobe.aem.accelerator.program.core.models.solr.SolrSearchRequest;
import com.adobe.aem.accelerator.program.core.models.solr.SolrSearchResponse;
import com.adobe.aem.accelerator.program.core.services.solr.SolrIndexListenerConfigurationService;
import com.adobe.aem.accelerator.program.core.services.solr.SolrQueryCommand;
import com.adobe.aem.accelerator.program.core.services.solr.SolrSearchService;
import com.adobe.aem.accelerator.program.core.services.solr.SolrServerConfigurationService;
import com.adobe.aem.accelerator.program.core.utils.solr.SolrSearchConstants;
import com.adobe.aem.accelerator.program.core.utils.solr.SolrUtils;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component(service = SolrSearchService.class, immediate = true)
public class SolrSearchServiceImpl implements SolrSearchService {

	private static final Logger LOG = LoggerFactory.getLogger(SolrSearchServiceImpl.class);

	@Reference
	private QueryBuilder queryBuilder;

	@Reference
	private SlingRepository repository;

	@Reference
	SolrServerConfigurationService solrConfigurationService;

	@Reference
	SolrQueryCommand solrQueryCommand;

	@Reference
	ResourceResolverFactory resolverFactory;

	@Reference
	SolrIndexListenerConfigurationService solrlistenerService;


	/**
	 * This method takes path and type of resource to perform search in JCR
	 *
	 * @param resourcePath
	 * @param resourceType
	 * @return JSONArray with resources metadata
	 */
	@Override
	public JSONArray crawlContent(String resourcePath, String resourceType) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("path", resourcePath);
		params.put("type", resourceType);
		params.put("p.offset", "0");
		params.put("p.limit", "10000");

		Session session = null;
		try {
			ResourceResolver resolver = SolrUtils.getResourceResolver(resolverFactory);
			session = resolver.adaptTo(Session.class);
			Query query = queryBuilder.createQuery(
					PredicateGroup.create(params), session);
			SearchResult searchResults = query.getResult();
			LOG.info("Found '{}' matches for query",
					searchResults.getTotalMatches());
			if (resourceType.equalsIgnoreCase("cq:PageContent")) {
				return createPageMetadataArray(searchResults);
			}

		} catch (RepositoryException e) {
			LOG.error("Exception due to", e);
		} catch (Exception e) {
			LOG.error("Exception due to", e);
		} finally {
			if (session.isLive() || session != null) {
				session.logout();
			}
		}
		return null;

	}

	/**
	 * This method takes search result of content pages and creates a JSON array
	 * object with properties
	 *
	 * @param results
	 * @return
	 * @throws RepositoryException
	 */

	@Override
	public JSONArray createPageMetadataArray(SearchResult results)
			throws RepositoryException {
		JSONArray solrDocs = new JSONArray();
		try {
			for (Hit hit : results.getHits()) {
				Resource pageContent = hit.getResource();
				ValueMap properties = pageContent.adaptTo(ValueMap.class);
				String isPageIndexable = properties.get(SolrSearchConstants.PROPERTY_EXCLUDE_FROM_INDEX,
						String.class);
				//check if the page is to be ignored by the indexing service
				String[] ignorePages = SolrUtils.getIndexDetails(SolrUtils.getResourceResolver(resolverFactory),"ignorePages");
				if (!SolrUtils.isPageIgnored(pageContent.getPath(), ignorePages, SolrUtils.getResourceResolver(resolverFactory))) {
					if (null != isPageIndexable && !isPageIndexable.equals(SolrSearchConstants.PROPERTY_EXCLUDE_FROM_INDEX_VALUE))
						continue;
					JSONObject propertiesMap = createPageMetadataObject(pageContent);
					solrDocs.put(propertiesMap);
				}
			}
		} catch(LoginException e) {
			LOG.error("Login Exception:" , e);
		}


		return solrDocs;

	}

	/**
	 * This method creates JSONObject which has all the page metadata which is used to index in Solr server
	 *
	 * @param It takes resource of type cq:PageContent to extract the page metadata
	 * @return Json object with page's metadata
	 */
	@Override
	public JSONObject createPageMetadataObject(Resource pageContent) {
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		try {
			Node jcrNode = pageContent.adaptTo(Node.class);
			ResourceResolver resolver = SolrUtils.getResourceResolver(resolverFactory);
			Session session = resolver.adaptTo(Session.class);
			propertiesMap.put("id", pageContent.getParent().getPath());
			propertiesMap.put("url", pageContent.getParent().getPath() + ".html");
			ValueMap properties = pageContent.adaptTo(ValueMap.class);
			String pageTitle = properties.get("jcr:title", String.class);
			if (StringUtils.isEmpty(pageTitle)) {
				pageTitle = pageContent.getParent().getName();
			}
			propertiesMap.put("title", pageTitle);
			propertiesMap.put("description", SolrUtils.checkNull(properties.get(
					"jcr:description", String.class)));
			propertiesMap.put("publishDate", SolrUtils.checkNull(properties.get(
					"publishdate", String.class)));
			propertiesMap.put("body", "");
			propertiesMap.put("lastModified", SolrUtils.solrDate(properties.get(
					"cq:lastModified", Calendar.class)));
			propertiesMap.put("contentType", "page");
			propertiesMap.put("tags", SolrUtils.getPageTags(pageContent));
			// child node props
			SearchResult childNodeSearchResult = SolrUtils.getAllChildPageNodes(queryBuilder, jcrNode.getParent(), session, StringUtils.EMPTY);
			Iterator<Node> childeNodeIter = childNodeSearchResult.getNodes();
			Node childNodeOfEachPage = null;
			while (childeNodeIter.hasNext()) {
				childNodeOfEachPage = childeNodeIter.next();
				SolrUtils.addPropertyToJSON(childNodeOfEachPage, propertiesMap, SolrUtils.getIndexDetails(resolver,"ignoreProperties"));
			}
		}catch(Exception e) {
			LOG.error("Exception at >>", e);
		}
		return new JSONObject(propertiesMap);
	}


	/**
	 * This method connects to the Solr server and indexes page content using Solrj api. This is used by bulk update handler (servlet)
	 *
	 * @param Takes Json array and iterates over each object and index to solr
	 * @return boolean true if it indexes successfully to solr server, else false.
	 */
	@Override
	public boolean indexPagesToSolr(JSONArray indexPageData,
									HttpSolrClient server) throws JSONException, SolrServerException,
			IOException {
		if (null != indexPageData) {
			for (int i = 0; i < indexPageData.length(); i++) {
				JSONObject pageJsonObject = indexPageData.getJSONObject(i);
				SolrInputDocument doc = createPageSolrDoc(pageJsonObject);
				server.add(doc);
			}
			server.commit();
			return true;
		}

		return false;
	}

	/**
	 * This method connects to the Solr server and indexes page content using Solrj api. This is used by transport handler
	 *
	 * @param Takes Json object and index to solr
	 * @return boolean true if it indexes successfully to solr server, else false.
	 */
	@Override
	public boolean indexPageToSolr(JSONObject indexPageData,
								   HttpSolrClient server) throws JSONException, SolrServerException,
			IOException {
		if (null != indexPageData) {
			SolrInputDocument doc = createPageSolrDoc(indexPageData);
			server.add(doc);
			server.commit();
			return true;
		}

		return false;
	}


	private SolrInputDocument createPageSolrDoc(JSONObject pageJsonObject) throws JSONException {
		SolrInputDocument doc = new SolrInputDocument();
		/*doc.addField("id", pageJsonObject.get("id"));
		doc.addField("title", pageJsonObject.get("title"));
		doc.addField("body", pageJsonObject.get("body"));
		doc.addField("url", pageJsonObject.get("url"));
		doc.addField("description", pageJsonObject.get("description"));
		doc.addField("lastModified", pageJsonObject.get("lastModified"));
		doc.addField("contentType", pageJsonObject.get("contentType"));
		doc.addField("tags", pageJsonObject.get("tags"));*/

		Iterator keys = pageJsonObject.keys();
		while(keys.hasNext()) {
			Object key = keys.next();
			Object keyvalue = pageJsonObject.get(key.toString());
			doc.addField(key.toString(), keyvalue);
		}

		return doc;

	}

	public SolrSearchResponse performFacetSearch(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		SolrSearchResponse searchResponse = new SolrSearchResponse();
		Gson gson = new Gson();
		QueryResponse solrResponse = null;
		try {
			SolrSearchRequest searchRequest = getSearchRequestFromParameter(request);
			solrResponse = executeSearch(searchRequest);
			if (solrResponse != null) {
				SolrDocumentList solrDocumentList = solrResponse.getResults();
				searchResponse.setSolrDocumentList(solrDocumentList);
				searchResponse.setTotal(solrDocumentList.getNumFound());
				searchResponse.setFacets(getFacetFields(solrResponse));
			}
		} catch (Exception e) {
			LOG.error("Error : ", e);
		}
		return searchResponse;
	}

	public QueryResponse executeSearch(SolrSearchRequest searchRequest) {

		QueryResponse solrResponse = null;
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setFacet(true);
		solrQuery.setRows(SolrSearchConstants.SOLR_RESULTS_ROWS);
		solrQueryCommand.setText(searchRequest.getText(), solrQuery);
		solrQueryCommand.setSort(searchRequest.getSort(), searchRequest.getSortOrder(), solrQuery);
		solrQueryCommand.setFilters(searchRequest.getFilters(), solrQuery);

		LOG.info(" SOLR Query : " + solrQuery.toString());
		try {
			HttpSolrClient server = prepareSolrServer();
			solrResponse = server.query(solrQuery);
		} catch (Exception e) {
			LOG.error(" Error while getting the solr response ", e);
			solrResponse = null;
		}
		return solrResponse;

	}

	public SolrSearchRequest getSearchRequestFromParameter(SlingHttpServletRequest request) {
		//String[] selectors = request.getRequestPathInfo().getSelectors();
		String text = request.getParameter("text");//getReqParamValue(selectors, request, "text");
		if (text.isEmpty()) {
			text = SolrSearchConstants.SOLR_QUERY_WILDCARD;
		}
		String filters = request.getParameter("filters");//getReqParamValue(selectors, request,"filters");
		SolrSearchRequest searchRequest = new SolrSearchRequest();
		searchRequest.setText(text);
		searchRequest.setFilters(filters);
		searchRequest.setFacets(true);
		return searchRequest;
	}

	private String getReqParamValue(String[] selectors, SlingHttpServletRequest request, String reqParam) {
		String reqParamValue = selectors.length > 0 ? selectors[0] : request.getParameter(reqParam);
		if (StringUtils.isNotBlank(reqParamValue)) {
			byte[] bytes = reqParamValue.getBytes(StandardCharsets.ISO_8859_1);
			reqParamValue = new String(bytes, StandardCharsets.UTF_8);
		}
		return reqParamValue;
	}

	public JSONObject getFacetFields(QueryResponse solrResponse) {
		FacetField solrFacets = null;
		JSONArray facetField = new JSONArray();
		JSONObject facetFields = new JSONObject();
		try {
			for (String solrFacetField : SolrSearchConstants.SOLR_FACET_FIELD) {
				solrFacets = solrResponse.getFacetField(solrFacetField);
				if(solrFacets != null) {
					List<FacetField.Count> facetEntries = solrFacets.getValues();
					JSONObject attr;
					if (solrFacets.getValues() != null) {
						for (FacetField.Count fcount : facetEntries) {
							attr = new JSONObject();
							attr.put("value", fcount.getName());
							attr.put("count", fcount.getCount());
							facetField.put(attr);
						}
					}
					facetFields.put(solrFacetField, facetField);
				}
			}
		} catch (JSONException e) {
			LOG.error("Exception in getting Facets : ", e);
		}
		return facetFields;
	}

	@Override
	public HttpSolrClient prepareSolrServer() {
		String protocol = solrConfigurationService.getSolrProtocol();
		String serverName = solrConfigurationService.getSolrServerName();
		String serverPort = solrConfigurationService.getSolrServerPort();
		String coreName = solrConfigurationService.getSolrCoreName();
		String pagesResourcePath = solrConfigurationService
				.getContentPagePath();
		String URL = protocol + "://" + serverName + ":" + serverPort
				+ "/solr/" + coreName;
		HttpSolrClient server = new HttpSolrClient(URL);
		return server;
	}

	@Override
	public void deleteByQuery(HttpSolrClient server, SolrQuery query, String pagePath) {
		try {
			QueryResponse solrResponse = server.query(query);
			SolrDocumentList solrDocumentList = solrResponse.getResults();
			if (solrDocumentList.getNumFound() > 0) {
				server.deleteByQuery(SolrSearchConstants.SOLRDOC_FIELD_ID +":\"" + pagePath + "\"");
				server.commit();
				server.close();
			}
		} catch (SolrServerException se) {
			LOG.error("Error while accessing server for deletion of index", se);
		} catch (IOException ioe) {
			LOG.error("Error while accessing server for deletion of index", ioe);
		}
	}

	@Override
	public void updateIndex(HttpSolrClient server, SolrQuery query, Resource res) {
		try {
			QueryResponse solrResponse = server.query(query);
			SolrDocumentList solrDocumentList = solrResponse.getResults();
			Resource resource = res.adaptTo(Page.class).getContentResource();
			JSONObject dataObject = createPageMetadataObject(resource);
			boolean resultindexingPages = indexPageToSolr(dataObject, server);
			if (resultindexingPages) {
				LOG.info("Indexed the resource at - " + res.getParent());
			}
		}catch (SolrServerException se) {
			LOG.error("Error while accessing server for updation of index", se);
		} catch (IOException ioe) {
			LOG.error("Error while accessing server for updation of index", ioe);
		}catch (JSONException json) {
			LOG.error("Error while accessing server for updation of index", json);
		}
	}


}