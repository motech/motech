package org.motechproject.cmslite.api.service;

import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.Content;
import org.motechproject.cmslite.api.model.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public interface CMSLiteService {
    InputStream getContent(String language, String name) throws ResourceNotFoundException;

    void addContent(Content content) throws CMSLiteException;
}