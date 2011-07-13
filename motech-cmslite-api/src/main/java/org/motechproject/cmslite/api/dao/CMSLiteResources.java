package org.motechproject.cmslite.api.dao;


import org.motechproject.cmslite.api.ResourceQuery;
import org.motechproject.cmslite.api.model.Resource;

public interface CMSLiteResources {

    Resource getResource(ResourceQuery query);
}
