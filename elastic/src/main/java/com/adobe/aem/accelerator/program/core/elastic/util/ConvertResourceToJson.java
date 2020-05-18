package com.adobe.aem.accelerator.program.core.elastic.util;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.jcr.JsonItemWriter;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example of how to easily turn a Node into a JSONObject.
 */
public class ConvertResourceToJson {

    /** The logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertResourceToJson.class);

    /**
     * Get the JSON representation of a Resource
     *
     * @param resolver Resolver to get resource
     * @param resource Resource to turn into JSON
     * @return JSON representation of the resource
     */
    public JSONObject resourceToJSON(final ResourceResolver resolver, final Resource resource) {
        final Node node = resource.adaptTo(Node.class);
        final StringWriter stringWriter = new StringWriter();
        final JsonItemWriter jsonWriter = new JsonItemWriter(null);

        JSONObject jsonObject = null;

        try {
            /* Get JSON with no limit to recursion depth. */
            jsonWriter.dump(node, stringWriter, -1);
            jsonObject = new JSONObject(stringWriter.toString());
        } catch (RepositoryException | JSONException e) {
            LOGGER.error("Could not create JSON", e);
        }

        return jsonObject;
    }
}