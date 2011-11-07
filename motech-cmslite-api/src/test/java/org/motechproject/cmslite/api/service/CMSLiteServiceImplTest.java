package org.motechproject.cmslite.api.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.cmslite.api.dao.AllStreamContents;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.ResourceNotFoundException;
import org.motechproject.cmslite.api.model.StreamContent;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CMSLiteServiceImplTest {
    @Mock
    private AllStreamContents allStreamContents;

    private CMSLiteService cmsLiteService;

    @Before
    public void setUp() {
        initMocks(this);

        cmsLiteService = new CMSLiteServiceImpl(allStreamContents);
    }

    @Test
    public void shouldReturnInputStreamToContentIfContentExists() throws IOException, ResourceNotFoundException {
        String language = "language";
        String name = "name";

        InputStream inputStreamToResource = mock(InputStream.class);
        StreamContent streamContent = new StreamContent(language, name, inputStreamToResource, "checksum", "audio/x-wav");

        when(allStreamContents.getStreamContent(language, name)).thenReturn(streamContent);

        InputStream content = cmsLiteService.getContent(language, name);

        verify(allStreamContents).getStreamContent(language, name);
        assertEquals(inputStreamToResource, content);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowExceptionIfContentDoesNotExist() throws ResourceNotFoundException {
        String language = "language";
        String name = "test1";

        when(allStreamContents.getStreamContent(language, name)).thenReturn(null);

        cmsLiteService.getContent(language, name);

        fail("Should have thrown ResourceNotFoundException when query is null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenQueryIsNull() throws ResourceNotFoundException {
        cmsLiteService.getContent(null, null);
        verify(allStreamContents, never()).getStreamContent(null, null);

        fail("Should have thrown IllegalArgumentException when query is null");
    }

    @Test
    public void shouldAddStreamContent() throws CMSLiteException {
        StreamContent streamContent = new StreamContent("language", "name", null, "checksum", "audio/x-wav");
        cmsLiteService.addContent(streamContent);

        verify(allStreamContents).addStreamContent(streamContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhileAddingContentWhenContentIsNull() throws CMSLiteException {
        cmsLiteService.addContent(null);

        fail("Should have thrown IllegalArgumentException when content is null.");
    }

}
