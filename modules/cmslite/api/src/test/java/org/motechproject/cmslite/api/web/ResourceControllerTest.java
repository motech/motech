package org.motechproject.cmslite.api.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.IsCollectionContaining;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItems;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.testing.utils.rest.RestTestUtil.jsonMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

public class ResourceControllerTest {
    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application", "json", Charset.forName("UTF-8"));

    private static final String STREAM_CHECKSUM = "checksum";
    private static final String STREAM_CONTENT_TYPE = "contentType";
    private static final String STREAM_LANGUAGE = "Spanish";
    private static final String STREAM_NAME = "valid-stream-name";

    private static final String STRING_LANGUAGE = "English";
    private static final String STRING_NAME = "valid-string-name";
    private static final String STRING_VALUE = "valid-value";

    private static final String STRING_TYPE = "string";
    private static final String STREAM_TYPE = "stream";

    @Mock
    private CMSLiteService cmsLiteService;

    @InjectMocks
    private ResourceController resourceController = new ResourceController();

    private MockMvc controller;

    @Before
    public void setUp() {
        initMocks(this);
        controller = standaloneSetup(resourceController).build();
    }

    @Test
    public void shouldReturnNamesStartedWithGivenTerm() throws Exception {
        StringContent stringContent = new StringContent(STRING_LANGUAGE, STRING_NAME, STRING_VALUE);
        StreamContent streamContent = new StreamContent(STREAM_LANGUAGE, STREAM_NAME, null, STREAM_CHECKSUM, STREAM_CONTENT_TYPE);
        String expectedResponse = createResponse(Arrays.asList(STRING_NAME));

        when(cmsLiteService.getAllContents()).thenReturn(Arrays.asList(stringContent, streamContent));

        controller.perform(
                get("/resource/available/{field}?term={term}", "name", "valid-stri")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
                content().string(jsonMatcher(expectedResponse))
        );

        verify(cmsLiteService).getAllContents();
    }

    @Test
    public void shouldReturnLanguagesStartedWithGivenTerm() throws Exception {
        String expectedResponse = createResponse(Arrays.asList(STREAM_LANGUAGE));

        controller.perform(
                get("/resource/available/{field}?term={term}", "language", "spa")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
                content().string(jsonMatcher(expectedResponse))
        );
    }

    @Test
    public void shouldGetContents() throws Exception {
        StringContent stringContent = new StringContent(STRING_LANGUAGE, STRING_NAME, STRING_VALUE);
        StreamContent streamContent = new StreamContent(STREAM_LANGUAGE, STREAM_NAME, null, STREAM_CHECKSUM, STREAM_CONTENT_TYPE);
        GridSettings settings = createGridSettings("", true, true, "", 5, 1, "", "asc");
        List<ResourceDto> resourceDtos = Arrays.asList(new ResourceDto(stringContent), new ResourceDto(streamContent));

        String expectedResponse = createResponse(new Resources(settings, resourceDtos));

        when(cmsLiteService.getAllContents()).thenReturn(Arrays.asList(stringContent, streamContent));

        controller.perform(
                get("/resource?name={name}&string={string}&stream={stream}&languages={languages}&rows={rows}&page={page}&sidx={sidx}&sord={sord}",
                        "", true, true, "", 5, 1, "", "asc")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
                content().string(jsonMatcher(expectedResponse))
        );

        verify(cmsLiteService).getAllContents();
    }

    @Test
    public void shouldReturnAllResourcesLanguages() throws Exception {
        StringContent stringContent = new StringContent(STRING_LANGUAGE, STRING_NAME, STRING_VALUE);
        StreamContent streamContent = new StreamContent(STREAM_LANGUAGE, STREAM_NAME, null, STREAM_CHECKSUM, STREAM_CONTENT_TYPE);

        when(cmsLiteService.getAllContents()).thenReturn(Arrays.asList(stringContent, streamContent));

        Set<String> actual = resourceController.getAllLanguages();
        assertThat(actual, hasItems(STREAM_LANGUAGE, STRING_LANGUAGE));
    }

    @Test
    public void shouldReturnStreamContent() throws Exception {
        StreamContent streamContent = new StreamContent(STREAM_LANGUAGE, STREAM_NAME, null, STREAM_CHECKSUM, STREAM_CONTENT_TYPE);
        String expectedResponse = createResponse(streamContent);

        when(cmsLiteService.getStreamContent(STREAM_LANGUAGE, STREAM_NAME)).thenReturn(streamContent);

        controller.perform(
                get("/resource/{type}/{language}/{name}", STREAM_TYPE, STREAM_LANGUAGE, STREAM_NAME)
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
                content().string(jsonMatcher(expectedResponse))
        );

        verify(cmsLiteService).getStreamContent(STREAM_LANGUAGE, STREAM_NAME);
    }

    @Test
    public void shouldReturnStringContent() throws Exception {
        StringContent stringContent = new StringContent(STRING_LANGUAGE, STRING_NAME, STRING_VALUE);
        String expectedResponse = createResponse(stringContent);

        when(cmsLiteService.getStringContent(STRING_LANGUAGE, STRING_NAME)).thenReturn(stringContent);

        controller.perform(
                get("/resource/{type}/{language}/{name}", STRING_TYPE, STRING_LANGUAGE, STRING_NAME)
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
                content().string(jsonMatcher(expectedResponse))
        );

        verify(cmsLiteService).getStringContent(STRING_LANGUAGE, STRING_NAME);
    }

    @Test
    public void shouldEditStreamContent() throws Exception {
        StreamContent streamContent = new StreamContent(STREAM_LANGUAGE, STREAM_NAME, null, STREAM_CHECKSUM, STREAM_CONTENT_TYPE);
        MultipartFile file = mock(MultipartFile.class);
        InputStream stream = mock(InputStream.class);

        when(cmsLiteService.getStreamContent(STREAM_LANGUAGE, STREAM_NAME)).thenReturn(streamContent);
        when(file.getInputStream()).thenReturn(stream);
        when(file.getBytes()).thenReturn("new file".getBytes());
        when(file.getContentType()).thenReturn("text/plain");

        resourceController.editStreamContent(STREAM_LANGUAGE, STREAM_NAME, file);

        ArgumentCaptor<StreamContent> captor = ArgumentCaptor.forClass(StreamContent.class);

        verify(cmsLiteService).addContent(captor.capture());

        streamContent.setInputStream(stream);
        streamContent.setContentType("text/plain");
        streamContent.setChecksum(md5Hex("new file".getBytes()));

        assertEquals(streamContent, captor.getValue());
    }

    @Test
    public void shouldEditStringContent() throws Exception {
        StringContent stringContent = new StringContent(STRING_LANGUAGE, STRING_NAME, STRING_VALUE);

        when(cmsLiteService.getStringContent(STRING_LANGUAGE, STRING_NAME)).thenReturn(stringContent);

        controller.perform(
                post("/resource/string/{language}/{name}", STRING_LANGUAGE, STRING_NAME)
                        .param("value", "new value")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        );

        ArgumentCaptor<StringContent> captor = ArgumentCaptor.forClass(StringContent.class);

        verify(cmsLiteService).addContent(captor.capture());
        stringContent.setValue("new value");

        assertEquals(stringContent, captor.getValue());
    }

    @Test(expected = CMSLiteException.class)
    public void shouldNotAddContentWithoutSpecifiedType() throws IOException, CMSLiteException {
        resourceController.addContent("", "", "", "", null);
    }

    @Test(expected = CMSLiteException.class)
    public void shouldNotAddContentWithoutName() throws IOException, CMSLiteException {
        resourceController.addContent(STRING_TYPE, "", "", "", null);
    }

    @Test(expected = CMSLiteException.class)
    public void shouldNotAddContentWithoutLanguage() throws IOException, CMSLiteException {
        resourceController.addContent(STREAM_TYPE, STREAM_NAME, "", "", null);
    }

    @Test(expected = CMSLiteException.class)
    public void shouldNotAddStringContentWithoutValue() throws IOException, CMSLiteException {
        resourceController.addContent(STRING_TYPE, STRING_NAME, STRING_LANGUAGE, "", null);
    }

    @Test(expected = CMSLiteException.class)
    public void shouldNotAddStringContentWhenItAlreadyExist() throws IOException, CMSLiteException {
        when(cmsLiteService.isStringContentAvailable(STRING_LANGUAGE, STRING_NAME)).thenReturn(true);

        resourceController.addContent(STRING_TYPE, STRING_NAME, STRING_LANGUAGE, STRING_VALUE, null);
    }

    @Test
    public void shouldAddStringContent() throws CMSLiteException, IOException {
        StringContent stringContent = new StringContent(STRING_LANGUAGE, STRING_NAME, STRING_VALUE);

        resourceController.addContent(STRING_TYPE, STRING_NAME, STRING_LANGUAGE, STRING_VALUE, null);

        ArgumentCaptor<StringContent> captor = ArgumentCaptor.forClass(StringContent.class);
        verify(cmsLiteService).addContent(captor.capture());

        assertEquals(stringContent, captor.getValue());
    }

    @Test(expected = CMSLiteException.class)
    public void shouldNotAddStreamContentWithoutFile() throws IOException, CMSLiteException {
        resourceController.addContent(STREAM_TYPE, STREAM_NAME, STREAM_LANGUAGE, null, null);
    }

    @Test(expected = CMSLiteException.class)
    public void shouldNotAddStremContentWhenItAlreadyExist() throws IOException, CMSLiteException {
        when(cmsLiteService.isStreamContentAvailable(STREAM_LANGUAGE, STREAM_NAME)).thenReturn(true);

        resourceController.addContent(STREAM_TYPE, STREAM_NAME, STREAM_LANGUAGE, null, null);
    }

    @Test
    public void shouldAddStreamContent() throws CMSLiteException, IOException {
        byte[] bytes = "new file".getBytes();
        String checksum = md5Hex(bytes);

        MultipartFile file = mock(MultipartFile.class);
        InputStream stream = mock(InputStream.class);
        StreamContent streamContent = new StreamContent(STREAM_LANGUAGE, STREAM_NAME, stream, checksum, STREAM_CONTENT_TYPE);

        when(file.getInputStream()).thenReturn(stream);
        when(file.getBytes()).thenReturn(bytes);
        when(file.getContentType()).thenReturn(STREAM_CONTENT_TYPE);

        resourceController.addContent(STREAM_TYPE, STREAM_NAME, STREAM_LANGUAGE, null, file);

        ArgumentCaptor<StreamContent> captor = ArgumentCaptor.forClass(StreamContent.class);
        verify(cmsLiteService).addContent(captor.capture());

        assertEquals(streamContent, captor.getValue());
    }

    @Test
    public void shouldRemoveStreamContent() throws Exception {
        controller.perform(
                delete("/resource/{type}/{language}/{name}", STREAM_TYPE, STREAM_LANGUAGE, STREAM_NAME)
        ).andExpect(
                status().is(HttpStatus.OK.value())
        );

        verify(cmsLiteService).removeStreamContent(STREAM_LANGUAGE, STREAM_NAME);
    }

    @Test
    public void shouldRemoveStringContent() throws Exception {
        controller.perform(
                delete("/resource/{type}/{language}/{name}", STRING_TYPE, STRING_LANGUAGE, STRING_NAME)
        ).andExpect(
                status().is(HttpStatus.OK.value())
        );

        verify(cmsLiteService).removeStringContent(STRING_LANGUAGE, STRING_NAME);
    }

    private String createResponse(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    private GridSettings createGridSettings(String name, Boolean string, Boolean stream, String languages,
                                            Integer rows, Integer page, String sortColumn, String sortDirection) {
        GridSettings settings = new GridSettings();
        settings.setLanguages(languages);
        settings.setName(name);
        settings.setStream(stream);
        settings.setString(string);

        settings.setPage(page);
        settings.setRows(rows);
        settings.setSortColumn(sortColumn);
        settings.setSortDirection(sortDirection);

        return settings;
    }

}
