package com.adobe.aem.accelerator.program.core.elastic.indexing.contentbuilder;


import com.adobe.aem.accelerator.program.core.elastic.indexing.config.ElasticSearchIndexConfiguration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ElasticSearchContentBuilderImpl implements ElasticSearchContentBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchContentBuilderImpl.class);

    @Reference
    ElasticSearchIndexConfiguration elasticSearchIndexConfiguration;

    protected String[] getIndexRules(String primaryType) {
        ElasticSearchIndexConfiguration config = elasticSearchIndexConfiguration;
        if (config != null) {
            if(primaryType.equals("dam:Asset")){
                return ArrayUtils.addAll(config.getIndexAssetRules(), getFixedRules());
            }else if(primaryType.equals("cq:Page")){
                return ArrayUtils.addAll(config.getIndexPageRules(), getFixedRules());
            }
        }
        return getFixedRules();
    }

    protected String[] getFixedRules() {
        return new String[0];
    }


    protected Map<String, Object> getProperties(Resource res, String[] properties) {
        //TODO: add support for * property
        ValueMap vm = res.getValueMap();
        Map<String, Object> ret = Arrays.stream(properties)
                .filter(property -> vm.containsKey(property))
                .collect(Collectors.toMap(Function.identity(), property -> vm.get(property)));

        for (Resource child : res.getChildren()) {
            Map<String, Object> props = getProperties(child, properties);
            props.entrySet().forEach(entry -> {
                if (!ret.containsKey(entry.getKey())) {
                    ret.put(entry.getKey(), entry.getValue());
                } else {
                    ret.put(entry.getKey(), mergeProperties(ret.get(entry.getKey()), entry.getValue()));
                }
            });
        }
        return ret;
    }

    private Object[] mergeProperties(Object obj1, Object obj2) {
        List<Object> tmp = new ArrayList<>();
        addProperty(tmp, obj1);
        addProperty(tmp, obj2);
        return tmp.toArray(new Object[tmp.size()]);
    }

    private void addProperty(List<Object> list, Object property) {
        if (property.getClass().isArray()) {
            list.addAll(Arrays.asList((Object[]) property));
        } else {
            list.add(property);
        }
    }
}
