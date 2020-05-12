package com.adobe.aem.accelerator.program.core.utils.solr;

public final class SolrSearchConstants
{

    private SolrSearchConstants() {
    }

    public static final String CONTENT_TYPE = "application/json";
    public static final String UTF_ENCODING = "UTF-8";
    public static final String CONSTANT_COLON = ":";
    public static final String CONSTANT_COMMA = ",";
    public static final String CONSTANT_SLASH = "/";
    public static final String JCR_CONTENT_NODE_NAME = "jcr:content";
    public static final String JCR_PRIMARY_TYPE = "cq:PageContent";
    public static final String SOLR_QUERY_WILDCARD = "*";
    public static final String SOLR_QUERY_Q = "q";

    public static final String TEXT_PARAM = "text";
    public static final String FILTERS_PARAM = "filters";

    public static final String[] SOLR_FACET_FIELD = { "Genre", "tags" };
    public static final int SOLR_RESULTS_ROWS = 500;

    public static final String SOLR_PROTOCOL = "solr.protocol";
    public static final String SOLR_SERVER_NAME = "solr.server.name";
    public static final String SOLR_SERVER_PORT = "solr.server.port";
    public static final String SOLR_CORE_NAME = "solr.core.name";
    public static final String SOLR_PAGEPATH = "solr.core.pagepath";
    public static final String SOLR_IGNORE_INDEX_PAGEPATH = "solr.ignore.pagepath";
    public static final String SOLR_INDEX_LISTENER_ENABLED = "solr.listener.enabled";
    public static final String SOLR_INDEX_PROPERTIES = "solr.ignore.properties";

    public static final String JCR_PRIMARY_TYPE_NT_UNSTRUCTURED = "nt:unstructured";

    public static final String QUERY_BUILDER_PATH = "path";
    public static final String QUERY_BUILDER_TYPE = "type";
    public static final String QUERY_BUILDER_NODENAME = "nodename";
    public static final String QUERY_BUILDER_OFFSET = "p.offset";
    public static final String QUERY_BUILDER_LIMIT = "p.limit";
    public static final String QUERY_BUILDER_OFFSET_DEFAULT = "0";
    public static final String QUERY_BUILDER_LIMIT_DEFAULT = "-1";

    public static final String TEST_USER = "testUser";
    public static final String PROPERTY_EXCLUDE_FROM_INDEX = "notIndexable";
    public static final String PROPERTY_EXCLUDE_FROM_INDEX_VALUE = "true";
    public static final String PAGE_PROPERTY_SOLR_INDEX = "solrindex";
    public static final String PAGE_PROPERTY_SOLR_INDEX_VALUE = "need index";

    public static final String SOLRDOC_FIELD_ID = "id";
    public static final String SOLRDOC_FIELD_URL = "url";


}

