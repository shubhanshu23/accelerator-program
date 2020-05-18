package com.adobe.aem.accelerator.program.core.elastic.search;

import com.adobe.aem.accelerator.program.core.elastic.indexing.config.ElasticSearchIndexConfiguration;
import org.apache.http.HttpHost;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component(service = Servlet.class, immediate = true, enabled = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "= Elastic Search Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/esearch"})
public class FullTextSearchServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(FullTextSearchServlet.class);

    @Reference
    ElasticSearchIndexConfiguration elasticSearchIndexConfiguration;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String query = request.getParameter("query");
        String filterBy = request.getParameter("filter");
        String sortBy = request.getParameter("sortby");
        String limit = request.getParameter("limit");
        String offset = request.getParameter("offset");
        if (query != null) {
            SearchRequest searchRequest = new SearchRequest(elasticSearchIndexConfiguration.getIndex());
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.from(Integer.parseInt(offset));
            searchSourceBuilder.size(Integer.parseInt(limit));
            //searchSourceBuilde
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(query,
                    "jcr:description", "jcr:title", "dc:description", "dc:title").fuzziness(2);
            //multimatch query
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(multiMatchQueryBuilder);
            //filters
            filterQuery(filterBy, searchSourceBuilder, boolQueryBuilder);
            //setting source
            searchSourceBuilder.query(boolQueryBuilder);
            sortResults(sortBy, searchSourceBuilder);

            searchSourceBuilder.aggregation(
                    AggregationBuilders.terms("Filter").field("cq:tags")
            );
            searchRequest.source(searchSourceBuilder);

            RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(
                    new HttpHost("localhost", 9200, "http")));
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(searchResponse.toString());
            restHighLevelClient.close();
        }

    }

    private void sortResults(String sortBy, SearchSourceBuilder searchSourceBuilder) {
        if (sortBy != null && sortBy.equals("lastModified")) {
            searchSourceBuilder.sort("jcr:lastModified", SortOrder.DESC);
        }
    }

    private void filterQuery(String filterBy, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {
        if (filterBy != null) {
            List<String> tags = Arrays.asList(filterBy.split(","));
            TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("cq:tags", tags);
            boolQueryBuilder.filter(termsQueryBuilder);
        }
    }

}
