package org.motechproject.cmslite.api.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.repository.AllStreamContents;
import org.motechproject.cmslite.api.repository.AllStringContents;

import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CMSLiteServiceImplTest {
    @Mock
    private AllStreamContents allStreamContents;
    @Mock
    private AllStringContents allStringContents;

    private CMSLiteService cmsLiteService;

    @Before
    public void setUp() {
        initMocks(this);

        cmsLiteService = new CMSLiteServiceImpl(allStreamContents, allStringContents);
    }

    @Test
    public void shouldReturnStreamContentIfContentExists() throws IOException, ContentNotFoundException {
        String language = "language";
        String name = "name";

        InputStream inputStreamToResource = mock(InputStream.class);
        StreamContent streamContent = new StreamContent(language, name, inputStreamToResource, "checksum", "audio/x-wav");

        when(allStreamContents.getContent(language, name)).thenReturn(streamContent);

        StreamContent content = cmsLiteService.getStreamContent(language, name);

        verify(allStreamContents).getContent(language, name);
        assertEquals(streamContent, content);
    }

    @Test
    public void shouldReturnStringContentIfContentExists() throws IOException, ContentNotFoundException {
        String language = "language";
        String name = "name";

        InputStream inputStreamToResource = mock(InputStream.class);
        StringContent stringContent = new StringContent(language, name, "value");

        when(allStringContents.getContent(language, name)).thenReturn(stringContent);

        StringContent content = cmsLiteService.getStringContent(language, name);

        verify(allStringContents).getContent(language, name);
        assertEquals(stringContent, content);
    }

    @Test(expected = ContentNotFoundException.class)
    public void shouldThrowExceptionIfStreamContentDoesNotExist() throws ContentNotFoundException {
        String language = "language";
        String name = "test1";

        when(allStreamContents.getContent(language, name)).thenReturn(null);

        cmsLiteService.getStreamContent(language, name);

        fail("Should have thrown ContentNotFoundException when query is null");
    }

    @Test(expected = ContentNotFoundException.class)
    public void shouldThrowExceptionIfStringContentDoesNotExist() throws ContentNotFoundException {
        String language = "language";
        String name = "test1";

        when(allStringContents.getContent(language, name)).thenReturn(null);

        cmsLiteService.getStringContent(language, name);

        fail("Should have thrown ContentNotFoundException when query is null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhileGettingContentWhenBothLanguageAndNameAreNull() throws ContentNotFoundException {
        cmsLiteService.getStreamContent(null, null);
        verify(allStreamContents, never()).getContent(null, null);

        fail("Should have thrown IllegalArgumentException when query is null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhileGettingContentWhenLanguageIsNull() throws ContentNotFoundException {
        cmsLiteService.getStringContent(null, "name");
        verify(allStringContents, never()).getContent(null, "name");

        fail("Should have thrown IllegalArgumentException when query is null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhileGettingContentWhenNameIsNull() throws ContentNotFoundException {
        cmsLiteService.getStringContent("en", null);
        verify(allStringContents, never()).getContent("en", null);

        fail("Should have thrown IllegalArgumentException when query is null");
    }

    @Test
    public void shouldAddStreamContent() throws CMSLiteException {
        StreamContent streamContent = new StreamContent("language", "name", null, "checksum", "audio/x-wav");
        cmsLiteService.addContent(streamContent);

        verify(allStreamContents).addContent(streamContent);
    }

    @Test
    public void shouldAddStringContent() throws CMSLiteException {
        StringContent stringContent = new StringContent("language", "name", "value");
        cmsLiteService.addContent(stringContent);

        verify(allStringContents).addContent(stringContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhileAddingContentWhenContentIsNull() throws CMSLiteException {
        cmsLiteService.addContent(null);

        fail("Should have thrown IllegalArgumentException when content is null.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhileAddingContentWhenLanguageIsNull
            () throws CMSLiteException {
        cmsLiteService.addContent(new StringContent(null, "name", "value"));

        fail("Should have thrown IllegalArgumentException when language is null.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhileAddingContentWhenNameIsNull
            () throws CMSLiteException {
        cmsLiteService.addContent(new StringContent("language", null, "value"));

        fail("Should have thrown IllegalArgumentException when name is null.");
    }

    @Test
    public void shouldReturnTrueIfStreamContentAvailable() {
        when(allStreamContents.isContentAvailable("language", "name")).thenReturn(true);
        assertTrue(cmsLiteService.isStreamContentAvailable("language", "name"));
    }

    @Test
    public void shouldReturnFalseIfStreamContentDoesNotAvailable() {
        when(allStreamContents.isContentAvailable("language", "name")).thenReturn(false);
        assertFalse(cmsLiteService.isStreamContentAvailable("language", "name"));
    }

    @Test
    public void shouldReturnTrueIfStringContentAvailable() {
        when(allStringContents.isContentAvailable("language", "name")).thenReturn(true);
        assertTrue(cmsLiteService.isStringContentAvailable("language", "name"));
    }

    @Test
    public void shouldReturnFalseIfStringContentNotAvailable() {
        when(allStringContents.isContentAvailable("language", "name")).thenReturn(false);
        assertFalse(cmsLiteService.isStringContentAvailable("language", "name"));
    }

}
