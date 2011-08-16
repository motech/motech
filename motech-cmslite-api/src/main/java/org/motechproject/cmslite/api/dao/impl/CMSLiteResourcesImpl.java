package org.motechproject.cmslite.api.dao.impl;

import org.ektorp.AttachmentInputStream;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.cmslite.api.CMSLiteException;
import org.motechproject.cmslite.api.ResourceQuery;
import org.motechproject.cmslite.api.dao.CMSLiteResources;
import org.motechproject.cmslite.api.model.Resource;
import org.motechproject.dao.MotechAuditableRepository;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class CMSLiteResourcesImpl extends MotechAuditableRepository<Resource> implements CMSLiteResources {

    protected CMSLiteResourcesImpl(@Qualifier("cmsLiteDatabase") CouchDbConnector db) {
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

    public void addResource(ResourceQuery query, InputStream inputStream) throws CMSLiteException {
        Resource resource = new Resource();
        resource.setName(query.getName());
        resource.setLanguage(query.getLanguage());

        try {
            String checksum = checksum(inputStream);
            Resource resourceFromDB = getResource(query);

            boolean create = (resourceFromDB == null);
            boolean sameAttachment = !create && !checksum.equals(resourceFromDB.getChecksum());

            if (!create && !sameAttachment) return;

            if (create) {
                resource.setChecksum(checksum);
                db.create(resource);
                createAttachment(inputStream, resource);
            } else {
                resourceFromDB.setChecksum(checksum);
                resourceFromDB.setInputStream(null);
                db.update(resourceFromDB);
                createAttachment(inputStream, resourceFromDB);
            }
        } catch (Exception e) {
            throw new CMSLiteException(e.getMessage(), e);
        }
    }

    private void createAttachment(InputStream inputStream, Resource resource) {
        AttachmentInputStream attachmentInputStream = new AttachmentInputStream(resource.getId(), inputStream, "audio/x-wav");
        db.createAttachment(resource.getId(), resource.getRevision(), attachmentInputStream);
    }

    public String checksum(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        byte[] buffer = new byte[8192];
        int read = 0;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        inputStream.mark(inputStream.available() + 1);
        while ((read = inputStream.read(buffer)) > 0) {
            messageDigest.update(buffer, 0, read);
        }
        inputStream.reset();

        return String.format("%1$032X", new BigInteger(1, messageDigest.digest()));
    }
}
