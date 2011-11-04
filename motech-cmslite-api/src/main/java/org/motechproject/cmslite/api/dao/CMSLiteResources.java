package org.motechproject.cmslite.api.dao;


import org.motechproject.cmslite.api.CMSLiteException;
import org.motechproject.cmslite.api.ResourceQuery;
import org.motechproject.cmslite.api.model.Resource;

import java.io.InputStream;

public interface CMSLiteResources {
    Resource getResourceFromDB(ResourceQuery query);

    void addResource(ResourceQuery query, InputStream inputStream, String checksum) throws CMSLiteException;
}