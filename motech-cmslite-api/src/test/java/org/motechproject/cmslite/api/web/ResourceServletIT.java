package org.motechproject.cmslite.api.web;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.cmslite.api.dao.AllStreamContents;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationCmsLiteApi.xml")
public class ResourceServletIT {

    public static final String CONTEXT_PATH = "/tama";
    public static final String WAVE_FILE = "background.wav";
    public static final String SERVLET_PATH = "/wav";
    public static final String LANGUAGE = "en";
    public static String REQUEST_URI = CONTEXT_PATH + SERVLET_PATH + "/" + LANGUAGE + "/" + WAVE_FILE;

    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpServletResponse httpServletResponse;
    @Mock
    ServletOutputStream servletOutputStream;
    @Autowired
    CMSLiteService cmsLiteService;
    @Autowired
    AllStreamContents allStreamContents;

    private ResourceServlet resourceServlet;

    @Before
    public void setUp() {
        initMocks(this);
        resourceServlet = new ResourceServlet();

        String pathToFile = "/" + WAVE_FILE;
        addResource(pathToFile);

        when(httpServletRequest.getRequestURI()).thenReturn(REQUEST_URI);
        when(httpServletRequest.getContextPath()).thenReturn(CONTEXT_PATH);
        when(httpServletRequest.getServletPath()).thenReturn(SERVLET_PATH);
        try {
            when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore("failing on some machines. shruthi/puneet will fix it soon.")
    public void doGet() {
        try {
            resourceServlet.doGet(httpServletRequest, httpServletResponse);
            verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
            verify(httpServletResponse).setHeader("Content-Type", "audio/x-wav");
            verify(httpServletResponse).setHeader("Accept-Ranges", "bytes");
            verify(httpServletResponse).setContentLength(240044);
            verify(servletOutputStream).flush();
            verify(servletOutputStream).close();
        } catch (ServletException e) {
            assertFalse(true);
        } catch (IOException e) {
            assertFalse(true);
        }
    }

    private void addResource(String pathToFile) {
        InputStream inputStreamToResource = this.getClass().getResourceAsStream(pathToFile);
        try {
            StreamContent streamContent = new StreamContent(LANGUAGE, WAVE_FILE, inputStreamToResource, null, "audio/x-wav");
            allStreamContents.addStreamContent(streamContent);
        } catch (CMSLiteException e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }
}
