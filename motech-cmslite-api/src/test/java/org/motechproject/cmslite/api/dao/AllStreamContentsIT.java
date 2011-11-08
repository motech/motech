package org.motechproject.cmslite.api.dao;

import org.ektorp.AttachmentInputStream;
import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationCmsLiteApi.xml")
public class AllStreamContentsIT {
    @Autowired
    AllStreamContents allStreamContents;
    @Autowired
    @Qualifier("cmsLiteDatabase")
    protected CouchDbConnector couchDbConnector;

    private StreamContent englishContent;

    @Test
    public void shouldAddStreamContent() throws CMSLiteException {
        String pathToFile = "/testResource.png";
        InputStream inputStreamToResource = this.getClass().getResourceAsStream(pathToFile);
        englishContent = new StreamContent("en", "test", inputStreamToResource, "checksum", "image/png");

        allStreamContents.addStreamContent(englishContent);

        StreamContent streamContent = couchDbConnector.get(StreamContent.class, englishContent.getId());
        assertNotNull(streamContent);
        assertEquals(englishContent.getName(), streamContent.getName());
        assertEquals(englishContent.getLanguage(), streamContent.getLanguage());
        assertEquals(englishContent.getChecksum(), streamContent.getChecksum());

        couchDbConnector.delete(streamContent);
    }

    @Test
    public void shouldGetStreamContent() {
        String pathToFile = "/testResource.png";
        InputStream inputStreamToResource = this.getClass().getResourceAsStream(pathToFile);
        englishContent = new StreamContent("en", "test", inputStreamToResource, "checksum", "image/png");

        couchDbConnector.create(englishContent);
        AttachmentInputStream attachmentInputStream = new AttachmentInputStream(englishContent.getId(), inputStreamToResource, englishContent.getContentType());
        couchDbConnector.createAttachment(englishContent.getId(), englishContent.getRevision(), attachmentInputStream);

        StreamContent streamContent = allStreamContents.getStreamContent(englishContent.getLanguage(), englishContent.getName());
        assertNotNull(streamContent);
        assertEquals(englishContent.getName(), streamContent.getName());
        assertEquals(englishContent.getLanguage(), streamContent.getLanguage());
        assertEquals(englishContent.getChecksum(), streamContent.getChecksum());

        couchDbConnector.delete(streamContent);
    }
}
