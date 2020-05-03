package com.adobe.aem.accelerator.program.core.utils.solr;

import org.apache.commons.lang3.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

public final class SolrUtils
{
    private SolrUtils() {
    }

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
}