package org.motechproject.cmslite.api.repository;

import org.apache.commons.io.IOUtils;
import org.ektorp.AttachmentInputStream;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.BufferedInputStream;
import java.util.List;

@Repository
@View(name = "by_language_and_name", map = "function(doc) { if (doc.type === 'StreamContent') emit([doc.language, doc.name], doc); }")
public class AllStreamContents extends BaseContentRepository<StreamContent> {
    @Autowired
    protected AllStreamContents(@Qualifier("cmsLiteDatabase") CouchDbConnector db) {
        super(StreamContent.class, db);
    }

    @Override
    public StreamContent getContent(String language, String name) {
        List<StreamContent> result = queryView("by_language_and_name", ComplexKey.of(language, name));

        if (result == null || result.isEmpty()) {
            return null;
        }

        StreamContent fetchedContent = result.get(0);
        AttachmentInputStream attachmentInputStream = db.getAttachment(fetchedContent.getId(), fetchedContent.getId());
        fetchedContent.setInputStream(attachmentInputStream);

        return fetchedContent;
    }

    @Override
    public boolean isContentAvailable(String language, String name) {
        return !queryView("by_language_and_name", ComplexKey.of(language, name)).isEmpty();
    }

    @Override
    public void addContent(StreamContent content) throws CMSLiteException {
        StreamContent streamContentFromDB = null;
        try {
            streamContentFromDB = getContent(content.getLanguage(), content.getName());

            boolean create = streamContentFromDB == null;
            if (!create && isSameAttachment(content, streamContentFromDB)) {
                return;
            }

            createOrUpdateContent(content, streamContentFromDB, create);
        } catch (Exception e) {
            throw new CMSLiteException(e.getMessage(), e);
        } finally {
            closeInputStream(content);
            closeInputStream(streamContentFromDB);
        }
    }

    private boolean isSameAttachment(StreamContent content, StreamContent streamContentFromDB) {
        return content.getChecksum().equals(streamContentFromDB.getChecksum());
    }

    private void closeInputStream(StreamContent content) {
        if (content != null) {
            IOUtils.closeQuietly(content.getInputStream());
        }
    }

    private void createOrUpdateContent(StreamContent streamContent, StreamContent streamContentFromDB, boolean resourceDoesNotExist) {
        if (resourceDoesNotExist) {
            db.create(streamContent);

            createAttachment(streamContent.getId(), streamContent.getRevision(), streamContent);
        } else {
            streamContentFromDB.setChecksum(streamContent.getChecksum());
            streamContentFromDB.setContentType(streamContent.getContentType());
            db.update(streamContentFromDB);

            createAttachment(streamContentFromDB.getId(), streamContentFromDB.getRevision(), streamContent);
        }
    }

    private void createAttachment(String contentId, String contentRevision, StreamContent content) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(content.getInputStream());

        AttachmentInputStream attachmentInputStream = new AttachmentInputStream(contentId, bufferedInputStream, content.getContentType());
        db.createAttachment(contentId, contentRevision, attachmentInputStream);
    }
}
