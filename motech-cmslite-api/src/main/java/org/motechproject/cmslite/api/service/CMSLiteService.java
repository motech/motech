package org.motechproject.cmslite.api.service;

import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.ResourceNotFoundException;
import org.motechproject.cmslite.api.model.ResourceQuery;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public interface CMSLiteService {

    InputStream getContent(ResourceQuery query) throws ResourceNotFoundException;

    void addContent(ResourceQuery query, InputStream inputStream, String md5Checksum) throws CMSLiteException;
}