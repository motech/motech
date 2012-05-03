package org.motechproject.server.verboice.it;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.verboice.VerboiceIVRController;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.motechproject.server.verboice.domain.VerboiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ivrVerboiceContext.xml"})
public class VerboiceIVRControllerIT {
    @Autowired
    private VerboiceIVRService verboiceIVRService;

    private VerboiceIVRController verboiceIVRController;

    @Before
    public void setUp() throws Exception {
        verboiceIVRController = new VerboiceIVRController(verboiceIVRService);
    }

    @Test
    public void shouldCallRegisteredHandlerDuringOutboundAndInboundCalls(){
        Map parameterMap = mock(Map.class);

        VerboiceHandler mockVerboiceHandler = mock(VerboiceHandler.class);
        final String verboiceResponseXml = "verboice response xml";

        when(mockVerboiceHandler.handle(parameterMap)).thenReturn(verboiceResponseXml);

        verboiceIVRService.registerHandler(mockVerboiceHandler);
        final HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getParameterMap()).thenReturn(parameterMap);

        assertThat(verboiceIVRController.handleRequest(mockRequest), is(equalTo(verboiceResponseXml)));
    }
}
