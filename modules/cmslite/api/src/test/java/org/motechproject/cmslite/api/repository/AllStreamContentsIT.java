package org.motechproject.cmslite.api.repository;

import org.ektorp.AttachmentInputStream;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.io.IOUtils.contentEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/META-INF/motech/*.xml")
public class AllStreamContentsIT {
    @Autowired
    AllStreamContents allStreamContents;
    @Autowired
    @Qualifier("cmsLiteDatabase")
    protected CouchDbConnector couchDbConnector;

    private StreamContent englishContent;

    @After
    public void tearDown() {
        if (englishContent != null && couchDbConnector.contains(englishContent.getId())) {
            StreamContent streamContent = couchDbConnector.get(StreamContent.class, englishContent.getId());
            couchDbConnector.delete(streamContent.getId(), streamContent.getRevision());
        }
    }

    @Test
    public void shouldAddStreamContent() throws CMSLiteException, IOException {
        String pathToFile = "/testResource.png";
        InputStream inputStreamToResource = this.getClass().getResourceAsStream(pathToFile);
        englishContent = new StreamContent("en", "test", inputStreamToResource, "checksum", "image/png");

        allStreamContents.addContent(englishContent);

        StreamContent streamContent = allStreamContents.getContent("en", "test");
        equalsStreamContent(englishContent, pathToFile, streamContent);
    }

    @Test
    public void shouldUpdateStreamContentAttachment() throws CMSLiteException, IOException {
        InputStream inputStreamToResource1 = this.getClass().getResourceAsStream("/background.wav");
        StreamContent file1 = new StreamContent("en", "test", inputStreamToResource1, "checksum1", "audio/x-wav");
        allStreamContents.addContent(file1);
        StreamContent streamContent1 = allStreamContents.getContent("en", "test");
        equalsStreamContent(file1, "/background.wav", streamContent1);

        String id1 = file1.getId();
        InputStream inputStreamToResource = this.getClass().getResourceAsStream("/10.wav");
        StreamContent file2 = new StreamContent("en", "test", inputStreamToResource, "checksum2", "audio/x-wav");
        allStreamContents.addContent(file2);
        StreamContent streamContent = allStreamContents.getContent("en", "test");
        equalsStreamContent(file2, "/10.wav", streamContent);

        couchDbConnector.delete(streamContent);
    }

    @Test
    public void shouldReturnTrueIfStreamContentAvailable() {
        createStreamContent();
        assertTrue(allStreamContents.isContentAvailable(englishContent.getLanguage(), englishContent.getName()));
    }

    @Test
    public void shouldReturnFalseIfStreamContentNotAvailable() {
        assertFalse(allStreamContents.isContentAvailable("en", "unknownContent"));
    }

    @Test
    public void shouldGetStreamContent() throws IOException {
        createStreamContent();

        StreamContent streamContent = allStreamContents.getContent(englishContent.getLanguage(), englishContent.getName());
        equalsStreamContent(englishContent, "/testResource.png", streamContent);
    }

    private void createStreamContent() {
        InputStream inputStreamToResource = this.getClass().getResourceAsStream("/testResource.png");
        englishContent = new StreamContent("en", "test", inputStreamToResource, "checksum", "image/png");

        couchDbConnector.create(englishContent);
        AttachmentInputStream attachmentInputStream = new AttachmentInputStream(englishContent.getId(), inputStreamToResource, englishContent.getContentType());
        couchDbConnector.createAttachment(englishContent.getId(), englishContent.getRevision(), attachmentInputStream);

        englishContent = allStreamContents.getContent("en", "test");
    }

    private void equalsStreamContent(StreamContent expected, String expectedInputStreamPath, StreamContent actual) throws IOException {
        // Content properties
        assertEquals(expected.getLanguage(), actual.getLanguage());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getMetadata(), actual.getMetadata());

        // StreamContent properties
        assertEquals(expected.getChecksum(), actual.getChecksum());
        assertEquals(expected.getContentType(), actual.getContentType());

        assertTrue("The actual and expected values of the inputStream property are not equal",
                contentEquals(this.getClass().getResourceAsStream(expectedInputStreamPath), actual.getInputStream()));
    }
}
