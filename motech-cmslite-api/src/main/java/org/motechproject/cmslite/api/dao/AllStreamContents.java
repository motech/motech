package org.motechproject.cmslite.api.dao;

import org.ektorp.AttachmentInputStream;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.dao.MotechAuditableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;

@Repository
@View(name = "all", map = "function(doc) { if (doc.type == 'STREAM_CONTENT') { emit(null, doc) } }")
public class AllStreamContents extends MotechAuditableRepository<StreamContent> {
    @Autowired
    protected AllStreamContents(CouchDbConnector db) {
        super(StreamContent.class, db);
    }

    @View(name = "by_language_and_name", map = "function(doc) { if (doc.type=='STREAM_CONTENT') { emit([doc.language, doc.name], doc); } }")
    public StreamContent getStreamContent(String language, String name) {
        ViewQuery query = createQuery("by_language_and_name").key(ComplexKey.of(language, name));
        List<StreamContent> result = db.queryView(query, StreamContent.class);

        if (result == null || result.isEmpty()) return null;

        StreamContent fetchedContent = result.get(0);
        AttachmentInputStream attachmentInputStream = db.getAttachment(fetchedContent.getId(), fetchedContent.getId());
        fetchedContent.setInputStream(attachmentInputStream);

        return fetchedContent;
    }

    public void addStreamContent(StreamContent streamContent) throws CMSLiteException {
        StreamContent streamContentFromDB = null;
        try {
            streamContentFromDB = getStreamContent(streamContent.getLanguage(), streamContent.getName());

            boolean create = streamContentFromDB == null;
            boolean sameAttachment = !create && !streamContent.getChecksum().equals(streamContentFromDB.getChecksum());

            if (!create && !sameAttachment) return;

            createOrUpdateContent(streamContent, streamContentFromDB, create);
            createAttachment(streamContent);
        } catch (Exception e) {
            throw new CMSLiteException(e.getMessage(), e);
        } finally {
            try {
                if (streamContentFromDB != null) {
                    streamContentFromDB.getInputStream().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createOrUpdateContent(StreamContent streamContent, StreamContent streamContentFromDB, boolean resourceDoesNotExist) throws IOException {
        if (resourceDoesNotExist)
            db.create(streamContent);
        else
            db.update(streamContentFromDB);
    }

    private void createAttachment(StreamContent streamContent) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(streamContent.getInputStream());

        AttachmentInputStream attachmentInputStream = new AttachmentInputStream(streamContent.getId(), bufferedInputStream, streamContent.getContentType());
        db.createAttachment(streamContent.getId(), streamContent.getRevision(), attachmentInputStream);
    }
}