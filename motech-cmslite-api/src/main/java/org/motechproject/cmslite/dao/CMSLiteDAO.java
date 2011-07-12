package org.motechproject.cmslite.dao;


import org.motechproject.cmslite.ResourceQuery;
import org.motechproject.cmslite.model.Resource;

public interface CMSLiteDAO {

    Resource getResource(ResourceQuery query);
}
