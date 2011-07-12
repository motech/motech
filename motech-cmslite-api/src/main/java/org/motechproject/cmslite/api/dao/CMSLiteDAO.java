package org.motechproject.cmslite.api.dao;


import org.motechproject.cmslite.api.ResourceQuery;
import org.motechproject.cmslite.api.model.Resource;

public interface CMSLiteDAO {

    Resource getResource(ResourceQuery query);
}
