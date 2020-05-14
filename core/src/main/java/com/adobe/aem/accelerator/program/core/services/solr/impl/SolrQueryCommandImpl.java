package com.adobe.aem.accelerator.program.core.services.solr.impl;

import com.adobe.aem.accelerator.program.core.services.solr.SolrQueryCommand;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = SolrQueryCommand.class, immediate = true)
public class SolrQueryCommandImpl implements SolrQueryCommand {
    private static final Logger LOG = LoggerFactory.getLogger(SolrQueryCommandImpl.class);
    
    @Override
    public void setText( String text, SolrQuery solrQuery ) {
        if ( StringUtils.isNotBlank( text ) ) {
            solrQuery.setQuery( text.trim() );
        }
    }

    @Override
    public void setSort(String sort, SolrQuery.ORDER sortOrder , SolrQuery solrQuery ) {
        if ( StringUtils.isNotBlank( sort ) ) {
        	String[] sorts = StringUtils.split(sort, ",");
        	for( String sf : sorts) {
        		sf = StringUtils.replace(sf, "_desc", "");
        		solrQuery.addSort(sf, sortOrder);
        	}
        }
    }

    @Override
    public void setRows( int rows, SolrQuery solrQuery ) {
        solrQuery.setRows( rows );
    }

    @Override
    public void setFilters( String filters, SolrQuery solrQuery ) {
        if (StringUtils.isNotBlank( filters )) {
            solrQuery.addFilterQuery( filters );
        }
    }
    

}