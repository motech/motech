package org.motechproject.server.voxeo.web;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CcmlControllerTest {
    @InjectMocks
    CcxmlController ccxmlController = new CcxmlController();

    @Mock
    private HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHandleRequest() {

        String incomingVXML = "http://foo.com/";
        String timeout = "99";
        String baseUrl = "http://bar.com/";
        String callTimeout = "100";

        Mockito.when(request.getParameter("incomingVXML")).thenReturn(incomingVXML);
        Mockito.when(request.getParameter("timeout")).thenReturn(timeout);
        Mockito.when(request.getParameter("baseUrl")).thenReturn(baseUrl);
        Mockito.when(request.getParameter("callTimeout")).thenReturn(callTimeout);

        ModelAndView modelAndView = ccxmlController.handleRequest(request, response);

        Assert.assertEquals("ccxml", modelAndView.getViewName());

        Assert.assertEquals(incomingVXML, modelAndView.getModelMap().get("incomingVXML"));
        Assert.assertEquals(timeout, modelAndView.getModelMap().get("timeout"));
        Assert.assertEquals(baseUrl, modelAndView.getModelMap().get("baseUrl"));
        Assert.assertEquals(callTimeout, modelAndView.getModelMap().get("callTimeout"));
    }
}
