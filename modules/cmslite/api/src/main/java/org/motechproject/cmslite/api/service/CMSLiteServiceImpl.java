package org.motechproject.cmslite.api.service;


import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.Content;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.repository.AllStreamContents;
import org.motechproject.cmslite.api.repository.AllStringContents;
import org.motechproject.cmslite.api.repository.BaseContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("cmsLiteService")
public class CMSLiteServiceImpl implements CMSLiteService {
    private AllStreamContents allStreamContents;
    private AllStringContents allStringContents;

    @Autowired
    public CMSLiteServiceImpl(AllStreamContents allStreamContents, AllStringContents allStringContents) {
        this.allStreamContents = allStreamContents;
        this.allStringContents = allStringContents;
    }

    @Override
    public StringContent getStringContent(String language, String name) throws ContentNotFoundException {
        return (StringContent) getContent(language, name, allStringContents);
    }

    @Override
    public StreamContent getStreamContent(String language, String name) throws ContentNotFoundException {
        return (StreamContent) getContent(language, name, allStreamContents);
    }

    @Override
    public void removeStreamContent(String language, String name) throws ContentNotFoundException {
        allStreamContents.remove(getStreamContent(language, name));
    }

    @Override
    public void removeStringContent(String language, String name) throws ContentNotFoundException {
        allStringContents.remove(getStringContent(language, name));
    }

    @Override
    public boolean isStreamContentAvailable(String language, String name) {
        return allStreamContents.isContentAvailable(language, name);
    }

    @Override
    public boolean isStringContentAvailable(String language, String name) {
        return allStringContents.isContentAvailable(language, name);
    }

    @Override
    public List<Content> getAllContents() {
        List<Content> contents = new ArrayList<>();
        contents.addAll(allStreamContents.getAll());
        contents.addAll(allStringContents.getAll());

        return contents;
    }

    private Content getContent(String language, String name, BaseContentRepository contentRepository) throws ContentNotFoundException {
        if (language == null || name == null) {
            throw new IllegalArgumentException("Language and Name should not be null");
        }

        Content content = contentRepository.getContent(language, name);
        if (content != null) {
            return content;
        }

        throw new ContentNotFoundException();
    }

    @Override
    public void addContent(Content content) throws CMSLiteException {
        if (content == null || content.getLanguage() == null || content.getName() == null) {
            throw new IllegalArgumentException("Content or language or name should not be null");
        }

        if (content instanceof StreamContent) {
            allStreamContents.addContent((StreamContent) content);
        } else if (content instanceof StringContent) {
            allStringContents.addContent((StringContent) content);
        }
    }
}
