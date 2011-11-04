package org.motechproject.cmslite.api.service;

import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.ResourceNotFoundException;
import org.motechproject.cmslite.api.model.ResourceQuery;

import java.io.InputStream;


public interface CMSLiteService {

    InputStream getContent(ResourceQuery query) throws ResourceNotFoundException;

    void addContent(ResourceQuery query, InputStream inputStream, String md5Checksum) throws CMSLiteException;
}
