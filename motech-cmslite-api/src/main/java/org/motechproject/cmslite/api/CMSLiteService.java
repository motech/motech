package org.motechproject.cmslite.api;


import org.motechproject.cmslite.api.dao.CMSLiteDAO;
import org.motechproject.cmslite.api.model.Resource;

import java.io.InputStream;

public class CMSLiteService {

    private CMSLiteDAO cmsLiteDAO;

    public InputStream getContent(ResourceQuery query) throws ResourceNotFoundException {
        if(query == null) throw new IllegalArgumentException("Query should not be null");
        Resource resource = cmsLiteDAO.getResource(query);
        if(resource != null) return resource.getResourceAsInputStream();
        throw  new ResourceNotFoundException();

    }

    public void setDAO(CMSLiteDAO cmsLiteDAO) {
        this.cmsLiteDAO = cmsLiteDAO;
    }
}
