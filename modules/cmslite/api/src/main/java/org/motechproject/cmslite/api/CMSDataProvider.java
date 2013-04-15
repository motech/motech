package org.motechproject.cmslite.api;

import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.commons.api.AbstractDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CMSDataProvider extends AbstractDataProvider {
    private static final String SUPPORT_FIELD = "id";

    private CMSLiteService cmsLiteService;

    @Autowired
    public void setCmsLiteService(CMSLiteService cmsLiteService) {
        this.cmsLiteService = cmsLiteService;
    }

    @Autowired
    public CMSDataProvider(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource("task-data-provider.json");
        if (resource != null) {
            setBody(resource);
        }
    }

    @Override
    public String getName() {
        return "CMS";
    }

    @Override
    public Object lookup(String type, Map<String, String> lookupFields) {
        Object obj = null;

        if (supports(type) && lookupFields.containsKey(SUPPORT_FIELD)) {
            String id = lookupFields.get(SUPPORT_FIELD);

            try {
                Class<?> cls = getClassForType(type);

                if (StringContent.class.isAssignableFrom(cls)) {
                    obj = getStringContent(id);
                } else if (StreamContent.class.isAssignableFrom(cls)) {
                    obj = getStreamContent(id);
                }

            } catch (ClassNotFoundException e) {
                logError(e.getMessage(), e);
            }
        }

        return obj;
    }

    @Override
    public List<Class<?>> getSupportClasses() {
        List<Class<?>> list = new ArrayList<>();
        list.add(StringContent.class);
        list.add(StreamContent.class);
        return list;
    }

    @Override
    public String getPackageRoot() {
        return "org.motechproject.cmslite.api.model";
    }

    private Object getStringContent(String stringContentId) {
        return cmsLiteService.getStringContent(stringContentId);
    }

    private Object getStreamContent(String streamContentId) {
        return cmsLiteService.getStreamContent(streamContentId);

    }
}
