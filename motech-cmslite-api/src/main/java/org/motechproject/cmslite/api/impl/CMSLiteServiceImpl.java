package org.motechproject.cmslite.api.impl;


import org.motechproject.cmslite.api.CMSLiteService;
import org.motechproject.cmslite.api.ResourceNotFoundException;
import org.motechproject.cmslite.api.ResourceQuery;
import org.motechproject.cmslite.api.dao.CMSLiteResources;
import org.motechproject.cmslite.api.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;

public class CMSLiteServiceImpl implements CMSLiteService{

    @Autowired
    private CMSLiteResources cmsLiteResources;

    public CMSLiteServiceImpl(CMSLiteResources resources) {
        this.cmsLiteResources = resources;
    }

    public InputStream getContent(ResourceQuery query) throws ResourceNotFoundException {
        if(query == null) throw new IllegalArgumentException("Query should not be null");
        Resource resource = cmsLiteResources.getResource(query);
        if(resource != null) return resource.getResourceAsInputStream();
        throw  new ResourceNotFoundException();

    }

}
