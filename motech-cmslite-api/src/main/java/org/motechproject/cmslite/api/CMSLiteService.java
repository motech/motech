package org.motechproject.cmslite.api;

import java.io.InputStream;


public interface CMSLiteService {

    InputStream getContent(ResourceQuery query) throws ResourceNotFoundException;

    void addContent(ResourceQuery query, InputStream inputStream, String md5Checksum) throws CMSLiteException;
}
