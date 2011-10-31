package org.motechproject.cmslite.api.impl;


import org.motechproject.cmslite.api.CMSLiteException;
import org.motechproject.cmslite.api.CMSLiteService;
import org.motechproject.cmslite.api.ResourceNotFoundException;
import org.motechproject.cmslite.api.ResourceQuery;
import org.motechproject.cmslite.api.dao.CMSLiteResources;
import org.motechproject.cmslite.api.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class CMSLiteServiceImpl implements CMSLiteService {

    @Autowired
    private CMSLiteResources cmsLiteResources;

    public CMSLiteServiceImpl(CMSLiteResources resources) {
        this.cmsLiteResources = resources;
    }

    public InputStream getContent(ResourceQuery query) throws ResourceNotFoundException {
        if (query == null) throw new IllegalArgumentException("Query should not be null");
        Resource resource = cmsLiteResources.getResource(query);
        if (resource != null) return resource.getResourceAsInputStream();
        throw new ResourceNotFoundException();

    }

    public void addContent(ResourceQuery query, InputStream inputStream, String md5Checksum) throws CMSLiteException {
        if (query == null) throw new IllegalArgumentException("Query should not be null");
        cmsLiteResources.addResource(query, inputStream, md5Checksum);
    }

}
