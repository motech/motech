package org.motechproject.cmslite.api.service;


import org.motechproject.cmslite.api.dao.AllStreamContents;
import org.motechproject.cmslite.api.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;

public class CMSLiteServiceImpl implements CMSLiteService {
    private AllStreamContents allStreamContents;

    @Autowired
    public CMSLiteServiceImpl(AllStreamContents allStreamContents) {
        this.allStreamContents = allStreamContents;
    }

    @Override
    public InputStream getContent(String language, String name) throws ResourceNotFoundException {
        if (language == null || name == null)
            throw new IllegalArgumentException("Language and Name should not be null");
        StreamContent resource = allStreamContents.getStreamContent(language, name);
        if (resource != null) return resource.getInputStream();

        throw new ResourceNotFoundException();
    }

    @Override
    public void addContent(Content content) throws CMSLiteException {
        if (content == null) throw new IllegalArgumentException("Content should not be null");

        if (content instanceof StreamContent)
            addStreamContent((StreamContent) content);
        else if (content instanceof StringContent)
            addStringContent((StringContent) content);
    }

    private void addStringContent(StringContent content) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void addStreamContent(StreamContent content) throws CMSLiteException {
        allStreamContents.addStreamContent(content);
    }
}