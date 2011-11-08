package org.motechproject.cmslite.api.dao;

import org.ektorp.AttachmentInputStream;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;

@Repository
@View(name = "all", map = "function(doc) { if (doc.type == 'STREAM_CONTENT') { emit(null, doc) } }")
public class AllStreamContents extends BaseContentRepository<StreamContent> {
    @Autowired
    protected AllStreamContents(CouchDbConnector db) {
        super(StreamContent.class, db);
    }

    @View(name = "by_language_and_name", map = "function(doc) { if (doc.type=='STREAM_CONTENT') { emit([doc.language, doc.name], doc); } }")
    @Override
    public StreamContent getContent(String language, String name) {
        ViewQuery query = createQuery("by_language_and_name").key(ComplexKey.of(language, name));
        List<StreamContent> result = db.queryView(query, StreamContent.class);

        if (result == null || result.isEmpty()) return null;

        StreamContent fetchedContent = result.get(0);
        AttachmentInputStream attachmentInputStream = db.getAttachment(fetchedContent.getId(), fetchedContent.getId());
        fetchedContent.setInputStream(attachmentInputStream);

        return fetchedContent;
    }

    @Override
    public void addContent(StreamContent content) throws CMSLiteException {
        StreamContent streamContentFromDB = null;
        try {
            streamContentFromDB = getContent(content.getLanguage(), content.getName());

            boolean create = streamContentFromDB == null;
            if (!create && isSameAttachment(content, streamContentFromDB)) return;

            createOrUpdateContent(content, streamContentFromDB, create);
        } catch (Exception e) {
            throw new CMSLiteException(e.getMessage(), e);
        } finally {
            try {
                closeInputStream(content);
                closeInputStream(streamContentFromDB);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isSameAttachment(StreamContent content, StreamContent streamContentFromDB) {
        return content.getChecksum().equals(streamContentFromDB.getChecksum());
    }

    private void closeInputStream(StreamContent content) throws IOException {
        if (content != null) {
            content.getInputStream().close();
        }
    }

    private void createOrUpdateContent(StreamContent streamContent, StreamContent streamContentFromDB, boolean resourceDoesNotExist) throws IOException {
        if (resourceDoesNotExist){
            db.create(streamContent);
            createAttachment(streamContent);
        }
        else {
            streamContentFromDB.setChecksum(streamContent.getChecksum());
            streamContentFromDB.setContentType(streamContent.getContentType());
            db.update(streamContentFromDB);
            createAttachment(streamContentFromDB);
        }
    }

    private void createAttachment(StreamContent streamContent) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(streamContent.getInputStream());

        AttachmentInputStream attachmentInputStream = new AttachmentInputStream(streamContent.getId(), bufferedInputStream, streamContent.getContentType());
        db.createAttachment(streamContent.getId(), streamContent.getRevision(), attachmentInputStream);
    }
}