package org.motechproject.cmslite.api.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.repository.AllStreamContents;
import org.motechproject.cmslite.api.repository.AllStringContents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/motech/applicationCmsLiteApi.xml")
public class ResourceControllerIT {

    public static final String NAME = "background.wav";
    public static final String LANGUAGE = "en";

    @Mock
    HttpServletResponse response;

    @Mock
    ServletOutputStream servletOutputStream;

    @Mock
    PrintWriter printWriter;

    @Autowired
    AllStreamContents allStreamContents;

    @Autowired
    AllStringContents allStringContents;

    @Autowired
    ResourceController controller;

    @Before
    public void setUp() throws IOException {
        initMocks(this);
    }

    @Test
    public void shouldGetStreamContent() throws IOException, CMSLiteException {
        addStreamContent("/" + NAME);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        controller.getStreamContent(LANGUAGE, NAME, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("audio/x-wav");
        verify(response).setHeader("Accept-Ranges", "bytes");
        verify(response).setContentLength(240044); // Length of the wav file
        verify(servletOutputStream).close();
    }

    @Test
    public void shouldGetStringContent() throws IOException, CMSLiteException {
        addStringContent("stringContent");
        when(response.getWriter()).thenReturn(printWriter);

        controller.getStringContent(LANGUAGE, NAME, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("text/plain");
        verify(response).setContentLength(13); // Length of the string text
        verify(printWriter).print("stringContent");
        verify(printWriter).close();
    }

    private void addStreamContent(String pathToFile) throws IOException, CMSLiteException {
        try (InputStream in = this.getClass().getResourceAsStream(pathToFile)) {
            StreamContent streamContent = new StreamContent(LANGUAGE, NAME, in, "checksum", "audio/x-wav");
            allStreamContents.addContent(streamContent);
        }
    }

    private void addStringContent(String stringValue) throws CMSLiteException {
        StringContent stringContent = new StringContent(LANGUAGE, NAME, stringValue);
        allStringContents.addContent(stringContent);
    }
}