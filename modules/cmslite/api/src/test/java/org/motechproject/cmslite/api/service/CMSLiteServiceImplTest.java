package org.motechproject.cmslite.api.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.Content;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.repository.AllStreamContents;
import org.motechproject.cmslite.api.repository.AllStringContents;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CMSLiteServiceImplTest {
    private static final String AUDIO_X_WAV = "audio/x-wav";
    private static final String CHECKSUM = "checksum";
    private static final String EN = "en";
    private static final String LANGUAGE = "language";
    private static final String NAME = "name";
    private static final String TEST_NAME = "test1";
    private static final String VALUE = "value";

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
    public void shouldReturnAllContents() throws ContentNotFoundException {
        InputStream inputStreamToResource = mock(InputStream.class);
        StreamContent streamContent = new StreamContent(LANGUAGE, NAME, inputStreamToResource, CHECKSUM, AUDIO_X_WAV);
        StringContent stringContent = new StringContent(LANGUAGE, NAME, VALUE);

        when(allStreamContents.getAll()).thenReturn(Arrays.asList(streamContent));
        when(allStringContents.getAll()).thenReturn(Arrays.asList(stringContent));

        List<Content> contents = cmsLiteService.getAllContents();

        verify(allStreamContents).getAll();
        verify(allStringContents).getAll();

        assertThat(contents, hasItems(streamContent, stringContent));
    }

    @Test
    public void shouldRemoveStreamContent() throws IOException, ContentNotFoundException {
        InputStream inputStreamToResource = mock(InputStream.class);
        StreamContent streamContent = new StreamContent(LANGUAGE, NAME, inputStreamToResource, CHECKSUM, AUDIO_X_WAV);

        when(allStreamContents.getContent(LANGUAGE, NAME)).thenReturn(streamContent);

        cmsLiteService.removeStreamContent(LANGUAGE, NAME);

        verify(allStreamContents).getContent(LANGUAGE, NAME);
        verify(allStreamContents).remove(streamContent);
    }

    @Test
    public void shouldRemoveStringContent() throws IOException, ContentNotFoundException {
        StringContent stringContent = new StringContent(LANGUAGE, NAME, VALUE);

        when(allStringContents.getContent(LANGUAGE, NAME)).thenReturn(stringContent);

        cmsLiteService.removeStringContent(LANGUAGE, NAME);

        verify(allStringContents).getContent(LANGUAGE, NAME);
        verify(allStringContents).remove(stringContent);
    }

    @Test
    public void shouldReturnStreamContentIfContentExists() throws IOException, ContentNotFoundException {
        InputStream inputStreamToResource = mock(InputStream.class);
        StreamContent streamContent = new StreamContent(LANGUAGE, NAME, inputStreamToResource, CHECKSUM, AUDIO_X_WAV);

        when(allStreamContents.getContent(LANGUAGE, NAME)).thenReturn(streamContent);

        StreamContent content = cmsLiteService.getStreamContent(LANGUAGE, NAME);

        verify(allStreamContents).getContent(LANGUAGE, NAME);
        assertEquals(streamContent, content);
    }

    @Test
    public void shouldReturnStringContentIfContentExists() throws IOException, ContentNotFoundException {
        StringContent stringContent = new StringContent(LANGUAGE, NAME, VALUE);

        when(allStringContents.getContent(LANGUAGE, NAME)).thenReturn(stringContent);

        StringContent content = cmsLiteService.getStringContent(LANGUAGE, NAME);

        verify(allStringContents).getContent(LANGUAGE, NAME);
        assertEquals(stringContent, content);
    }

    @Test(expected = ContentNotFoundException.class)
    public void shouldThrowExceptionIfStreamContentDoesNotExist() throws ContentNotFoundException {
        when(allStreamContents.getContent(LANGUAGE, TEST_NAME)).thenReturn(null);

        cmsLiteService.getStreamContent(LANGUAGE, TEST_NAME);
    }

    @Test(expected = ContentNotFoundException.class)
    public void shouldThrowExceptionIfStringContentDoesNotExist() throws ContentNotFoundException {
        when(allStringContents.getContent(LANGUAGE, TEST_NAME)).thenReturn(null);

        cmsLiteService.getStringContent(LANGUAGE, TEST_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhileGettingContentWhenBothLanguageAndNameAreNull() throws ContentNotFoundException {
        cmsLiteService.getStreamContent(null, null);
        verify(allStreamContents, never()).getContent(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhileGettingContentWhenLanguageIsNull() throws ContentNotFoundException {
        cmsLiteService.getStringContent(null, NAME);
        verify(allStringContents, never()).getContent(null, NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhileGettingContentWhenNameIsNull() throws ContentNotFoundException {
        cmsLiteService.getStringContent(EN, null);
        verify(allStringContents, never()).getContent(EN, null);
    }

    @Test
    public void shouldAddStreamContent() throws CMSLiteException {
        StreamContent streamContent = new StreamContent(LANGUAGE, NAME, null, CHECKSUM, AUDIO_X_WAV);
        cmsLiteService.addContent(streamContent);

        verify(allStreamContents).addContent(streamContent);
    }

    @Test
    public void shouldAddStringContent() throws CMSLiteException {
        StringContent stringContent = new StringContent(LANGUAGE, NAME, VALUE);
        cmsLiteService.addContent(stringContent);

        verify(allStringContents).addContent(stringContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhileAddingContentWhenContentIsNull() throws CMSLiteException {
        cmsLiteService.addContent(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhileAddingContentWhenLanguageIsNull
            () throws CMSLiteException {
        cmsLiteService.addContent(new StringContent(null, NAME, VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhileAddingContentWhenNameIsNull
            () throws CMSLiteException {
        cmsLiteService.addContent(new StringContent(LANGUAGE, null, VALUE));
    }

    @Test
    public void shouldReturnTrueIfStreamContentAvailable() {
        when(allStreamContents.isContentAvailable(LANGUAGE, NAME)).thenReturn(true);
        assertTrue(cmsLiteService.isStreamContentAvailable(LANGUAGE, NAME));
    }

    @Test
    public void shouldReturnFalseIfStreamContentDoesNotAvailable() {
        when(allStreamContents.isContentAvailable(LANGUAGE, NAME)).thenReturn(false);
        assertFalse(cmsLiteService.isStreamContentAvailable(LANGUAGE, NAME));
    }

    @Test
    public void shouldReturnTrueIfStringContentAvailable() {
        when(allStringContents.isContentAvailable(LANGUAGE, NAME)).thenReturn(true);
        assertTrue(cmsLiteService.isStringContentAvailable(LANGUAGE, NAME));
    }

    @Test
    public void shouldReturnFalseIfStringContentNotAvailable() {
        when(allStringContents.isContentAvailable(LANGUAGE, NAME)).thenReturn(false);
        assertFalse(cmsLiteService.isStringContentAvailable(LANGUAGE, NAME));
    }

}
