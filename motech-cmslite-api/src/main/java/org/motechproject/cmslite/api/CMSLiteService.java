package org.motechproject.cmslite.api;


import org.motechproject.cmslite.api.dao.CMSLiteResources;
import org.motechproject.cmslite.api.model.Resource;

import java.io.InputStream;

public class CMSLiteService {

    private CMSLiteResources cmsLiteResources;

    public InputStream getContent(ResourceQuery query) throws ResourceNotFoundException {
        if(query == null) throw new IllegalArgumentException("Query should not be null");
        Resource resource = cmsLiteResources.getResource(query);
        if(resource != null) return resource.getResourceAsInputStream();
        throw  new ResourceNotFoundException();

    }

    public void setDAO(CMSLiteResources cmsLiteResources) {
        this.cmsLiteResources = cmsLiteResources;
    }
}
