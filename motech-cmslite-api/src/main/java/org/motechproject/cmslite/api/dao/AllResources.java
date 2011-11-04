package org.motechproject.cmslite.api.dao;

import org.ektorp.AttachmentInputStream;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.ResourceQuery;
import org.motechproject.cmslite.api.model.Resource;
import org.motechproject.dao.MotechAuditableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Repository
public class AllResources extends MotechAuditableRepository<Resource> {
    @Autowired
    protected AllResources(@Qualifier("cmsLiteDatabase") CouchDbConnector db) {
        super(Resource.class, db);
    }

    @View(name = "by_name_and_language", map = "function(doc) { if (doc.type=='RESOURCE') { emit([doc.name, doc.language], doc); } }")
    public Resource getResource(ResourceQuery resourceQuery) {
        ViewQuery query = new ViewQuery().designDocId("_design/Resource").viewName("by_name_and_language").key(ComplexKey.of(resourceQuery.getName(), resourceQuery.getLanguage()));
        List<Resource> result = db.queryView(query, Resource.class);

        if (result == null || result.isEmpty()) return null;

        Resource fetchedResource = result.get(0);
        AttachmentInputStream attachmentInputStream = db.getAttachment(fetchedResource.getId(), fetchedResource.getId());
        fetchedResource.setInputStream(attachmentInputStream);
        return fetchedResource;
    }

    public void addResource(ResourceQuery query, InputStream inputStream, String checksum) throws CMSLiteException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        Resource resource = query.getResource();

        Resource resourceFromDB = null;
        try {
            resourceFromDB = getResource(query);

            boolean create = resourceFromDB == null;
            boolean sameAttachment = !create && !checksum.equals(resourceFromDB.getChecksum());

            if (!create && !sameAttachment) return;

            createOrUpdateResource(checksum, bufferedInputStream, resource, resourceFromDB, create);

        } catch (Exception e) {
            throw new CMSLiteException(e.getMessage(), e);
        } finally {
            try {
                if (resourceFromDB != null) {
                    resourceFromDB.getResourceAsInputStream().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createOrUpdateResource(String checksum, BufferedInputStream bufferedInputStream, Resource resource, Resource resourceFromDB, boolean resourceDoesNotExist) throws IOException {
        if (resourceDoesNotExist)
            createResource(checksum, bufferedInputStream, resource);
        else
            updateResource(checksum, bufferedInputStream, resourceFromDB);
    }

    private void updateResource(String checksum, BufferedInputStream bufferedInputStream, Resource resourceFromDB) throws IOException {
        resourceFromDB.setChecksum(checksum);
        db.update(resourceFromDB);
        createAttachment(bufferedInputStream, resourceFromDB);
    }

    private void createResource(String checksum, BufferedInputStream bufferedInputStream, Resource resource) throws IOException {
        resource.setChecksum(checksum);
        db.create(resource);
        createAttachment(bufferedInputStream, resource);
    }

    private void createAttachment(InputStream inputStream, Resource resource) throws IOException {
        AttachmentInputStream attachmentInputStream = new AttachmentInputStream(resource.getId(), inputStream, "audio/x-wav");
        db.createAttachment(resource.getId(), resource.getRevision(), attachmentInputStream);
    }
}
