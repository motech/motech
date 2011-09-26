package org.motechproject.ivr.kookoo.action.event;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.mockito.Mock;
import org.motechproject.eventtracking.service.EventService;
import org.motechproject.server.service.ivr.IVRCallIdentifiers;
import org.motechproject.server.service.ivr.IVRMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public abstract class BaseActionTest {
    @Mock
    protected HttpServletRequest request;
    @Mock
    protected HttpServletResponse response;
    @Mock
    protected HttpSession session;
    @Mock
    protected IVRMessage messages;
    @Mock
    protected EventService eventService;
    @Mock
    protected IVRCallIdentifiers callIdentifiers;

    @Before
    public void setUp() {
        initMocks(this);
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        mockIVRMessage();
    }

    protected void mockIVRMessage() {
        when(messages.getSignatureMusic()).thenReturn("http://music");
    }

    protected String sanitize(String responseXML) {
        return StringUtils.replace(responseXML, System.getProperty("line.separator"), "");
    }
}
