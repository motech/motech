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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class CMSLiteResourcesImpl extends MotechAuditableRepository<Resource> implements CMSLiteResources {

    protected CMSLiteResourcesImpl(@Qualifier("cmsLiteDatabase") CouchDbConnector db) {
        super(Resource.class, db);
    }

    @View(name = "by_name_and_language", map = "function(doc) { if (doc.type=='RESOURCE') { emit([doc.name, doc.language], doc); } }")
    public Resource getResourceFromDB(ResourceQuery resourceQuery) {
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
//            String checksum = checksum(bufferedInputStream);
            resourceFromDB = getResourceFromDB(query);

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
