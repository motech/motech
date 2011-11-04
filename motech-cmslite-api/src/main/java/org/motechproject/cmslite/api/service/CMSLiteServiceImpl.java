package org.motechproject.cmslite.api.service;


import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.ResourceNotFoundException;
import org.motechproject.cmslite.api.model.ResourceQuery;
import org.motechproject.cmslite.api.dao.AllResources;
import org.motechproject.cmslite.api.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class CMSLiteServiceImpl implements CMSLiteService {
    private AllResources allResources;

    @Autowired
    public CMSLiteServiceImpl(AllResources allResources) {
        this.allResources = allResources;
    }

    public InputStream getContent(ResourceQuery query) throws ResourceNotFoundException {
        if (query == null) throw new IllegalArgumentException("Query should not be null");
        Resource resource = allResources.getResource(query);
        if (resource != null) return resource.getResourceAsInputStream();

        throw new ResourceNotFoundException();
    }

    public void addContent(ResourceQuery query, InputStream inputStream, String md5Checksum) throws CMSLiteException {
        if (query == null) throw new IllegalArgumentException("Query should not be null");
        allResources.addResource(query, inputStream, md5Checksum);
    }

}
