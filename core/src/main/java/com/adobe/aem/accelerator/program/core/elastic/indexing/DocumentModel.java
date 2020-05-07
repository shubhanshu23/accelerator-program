package com.adobe.aem.accelerator.program.core.elastic.indexing;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DocumentModel {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentModel.class);

    @Getter
    @Setter
    private String index;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String id;

    @Setter
    private Map<String, Object> content = new HashMap<>();

    public DocumentModel() {
    }

    /**
     * @param index
     * @param type
     * @param path
     */
    public DocumentModel(String index, String type, String path) {
        this.index = index;
        this.type = type;
        setPath(path);
    }

    public void addContent(String key, Object value) {
        content.put(key, value);
    }

    public void setPath(String path) {
        this.content.put("path", path);
        this.id = DigestUtils.md5Hex(path);
    }

    public String getPath() {
        return getContent("path", String.class);
    }

    public void addContent(Map<String, Object> properties) {
        content.putAll(properties);
    }

    /**
     * @return unmodifiable map with the content
     */
    public Map<String, Object> getContent() {
        return Collections.unmodifiableMap(content);
    }

    public <T> T getContent(final String key, final Class<T> type) {
        try {
            return (T) content.get(key);
        } catch (ClassCastException cce) {
            LOG.warn("Could not cast " + key + " to " + type, cce);
        }
        return null;
    }

    public static Logger getLOG() {
        return LOG;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }
}
