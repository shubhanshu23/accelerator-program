package com.adobe.aem.accelerator.program.core.elastic.search;

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
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = Servlet.class, immediate = true, enabled = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "= Elastic Search Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/esearch"})
public class FullTextSearchServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(FullTextSearchServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String query = request.getParameter("query");
        String filterBy = request.getParameter("filter");
        String sortBy = request.getParameter("sortby");
        if (query != null) {
            SearchRequest searchRequest = new SearchRequest("idx");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(query,
                    "jcr:description", "jcr:title", "dc:description", "dc:title").fuzziness(2);
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(multiMatchQueryBuilder);
            if (filterBy != null) {
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("cq:tags", filterBy.trim());
                boolQueryBuilder.must(termQueryBuilder);

                //FOR_AGGREGATION
//                AggregationBuilder aggregationBuilder = AggregationBuilders.filters("tags",
//                        new FiltersAggregator.KeyedFilter("tagged",termQueryBuilder));
//                searchSourceBuilder.aggregation(aggregationBuilder);
            }
            searchSourceBuilder.query(boolQueryBuilder);
            if (sortBy != null) {
                searchSourceBuilder.sort("jcr:lastModified", SortOrder.DESC);
            }
            searchRequest.source(searchSourceBuilder);

            RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(
                    new HttpHost("localhost", 9200, "http")));
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            response.getWriter().write(searchResponse.toString());
            restHighLevelClient.close();
        }

    }

}
