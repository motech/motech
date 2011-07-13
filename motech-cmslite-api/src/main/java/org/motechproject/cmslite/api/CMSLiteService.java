package org.motechproject.cmslite.api;

import java.io.InputStream;

public interface CMSLiteService {

    InputStream getContent(ResourceQuery query) throws ResourceNotFoundException;
}
