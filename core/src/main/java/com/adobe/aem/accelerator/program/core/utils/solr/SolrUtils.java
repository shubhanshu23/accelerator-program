package com.adobe.aem.accelerator.program.core.utils.solr;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;

public final class SolrUtils
{
    private SolrUtils() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(SolrUtils.class);

    public static String[] getPageTags(final Resource pageContent) {
        final Page page = (Page)pageContent.getParent().adaptTo((Class)Page.class);
        final Tag[] tags = page.getTags();
        final String[] tagsArray = new String[tags.length];
        for (int i = 0; i < tags.length; ++i) {
            final Tag tag = tags[i];
            tagsArray[i] = tag.getTitle();
        }
        return tagsArray;
    }

    public static String solrDate(final Calendar cal) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD'T'hh:mm:ss");
        return dateFormat.format(cal.getTime()) + "Z";
    }

    public static String checkNull(final String property) {
        if (StringUtils.isEmpty((CharSequence)property)) {
            return "";
        }
        return property;
    }

    public static ResourceResolver getResourceResolver(ResourceResolverFactory resolverFactory) throws LoginException {
        final Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, SolrSearchConstants.TEST_USER);
        if (resolverFactory != null) {
            return resolverFactory.getServiceResourceResolver(param);
        }
        return null;
    }

    public static boolean isPageIgnored(final String pagePath, final String[] ignoredPages, ResourceResolver resolver) {
        if(ignoredPages != null) {
            for (String ignoredPath : ignoredPages) {
                if (pagePath.contains(ignoredPath) ){
                    return true;
                }
            }
        }
        return false;
    }

    public static  SearchResult getAllChildPageNodes(QueryBuilder queryBuilder, Node jcrNode, Session session, String nodeName)
            throws RepositoryException {
        SearchResult queryResponse = null;
        Map<String, String> queryParams = new HashMap<String, String>();
        defaultQueryParamsMap(queryParams, jcrNode.getPath(), SolrSearchConstants.JCR_PRIMARY_TYPE_NT_UNSTRUCTURED,
                nodeName);
        Query query = queryBuilder.createQuery(PredicateGroup.create(queryParams), session);
        queryResponse = query.getResult();
        return queryResponse;
    }

    public static void defaultQueryParamsMap(Map<String, String> queryParams, String indexContentPath, String nodeType,
                                       String nodeName) {
        queryParams.put(SolrSearchConstants.QUERY_BUILDER_PATH, indexContentPath);
        queryParams.put(SolrSearchConstants.QUERY_BUILDER_TYPE, nodeType);
        if (StringUtils.isNotBlank(nodeName)) {
            queryParams.put(SolrSearchConstants.QUERY_BUILDER_NODENAME, nodeName + "*");
        }
        queryParams.put(SolrSearchConstants.QUERY_BUILDER_OFFSET, SolrSearchConstants.QUERY_BUILDER_OFFSET_DEFAULT);
        queryParams.put(SolrSearchConstants.QUERY_BUILDER_LIMIT, SolrSearchConstants.QUERY_BUILDER_LIMIT_DEFAULT);
    }

    public static void addPropertyToJSON(Node jcrNode, Map<String, Object> propertiesMap, String[] ignorePropertyList)
            throws RepositoryException, JSONException {
        for (PropertyIterator propertyIterator = jcrNode.getProperties(); propertyIterator.hasNext(); ) {
            javax.jcr.Property property = propertyIterator.nextProperty();
            List<String> ignorePropertyFinalList = Arrays.asList(ignorePropertyList);
            ArrayList<Object> multiPropertyValueList = new ArrayList<Object>();
            if (null != property && null != ignorePropertyFinalList
                    && !ignorePropertyFinalList.contains(property.getName())) {
                if (property.isMultiple()) {
                    Value[] eachValueOfPropertyArray = property.getValues();
                    for (Value eachValueOfProperty : eachValueOfPropertyArray) {
                        if (null != eachValueOfProperty) {
                            if(eachValueOfProperty.getType() == PropertyType.BINARY) {
                                multiPropertyValueList.add(eachValueOfProperty.getBinary());
                            } else if(eachValueOfProperty.getType() == PropertyType.BOOLEAN) {
                                multiPropertyValueList.add(eachValueOfProperty.getBoolean());
                            }else if(eachValueOfProperty.getType() == PropertyType.STRING) {
                                multiPropertyValueList.add(eachValueOfProperty.getString());
                            }else if(eachValueOfProperty.getType() == PropertyType.LONG) {
                                multiPropertyValueList.add(eachValueOfProperty.getLong());
                            }else if(eachValueOfProperty.getType() == PropertyType.DECIMAL) {
                                multiPropertyValueList.add(eachValueOfProperty.getDecimal());
                            }else if(eachValueOfProperty.getType() == PropertyType.DATE) {
                                multiPropertyValueList.add(eachValueOfProperty.getDate());
                            }
                        }
                    }
                    propertiesMap.put(property.getName(), multiPropertyValueList);
                }else {
                    Value value = property.getValue();
                    if(value.getType() == PropertyType.BINARY) {
                        propertiesMap.put(property.getName(), value.getBinary());
                    } else if(value.getType() == PropertyType.DOUBLE) {
                        propertiesMap.put(property.getName(), value.getDouble());
                    }else if(value.getType() == PropertyType.BOOLEAN) {
                        propertiesMap.put(property.getName(), value.getBoolean());
                    }else if(value.getType() == PropertyType.DATE) {
                        propertiesMap.put(property.getName(), value.getDate());
                    }else if(value.getType() == PropertyType.LONG) {
                        propertiesMap.put(property.getName(), value.getLong());
                    }else if(value.getType() == PropertyType.STRING) {
                        propertiesMap.put(property.getName(), value.getString());
                    }
                }
            }
        }
    }
    public static String[] getIndexDetails(ResourceResolver resolver, String propertyName) {
        String[] strArray = null;
        List<String> ignorePages = new ArrayList();
        try {
            Resource res = resolver.getResource(SolrSearchConstants.SOLR_INDEXING_PROP_PATH);
            if (res != null) {
                Property property = res.adaptTo(Node.class).getProperty(propertyName);
                Value[] values = null;
                if (property.isMultiple()) {
                   values  = property.getValues();
                    for(Value val : values){
                        String page = val.getString();
                        ignorePages.add(page);
                    }
                }else {
                    Value value = property.getValue();
                    ignorePages.add(value.getString());
                }
            strArray = ignorePages.toArray(new String[ignorePages.size()]);
            }
        } catch(Exception e) {
            LOG.error("Exception occured at >>", e);
        }
        return strArray;
    }
}