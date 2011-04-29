package org.motechproject.server.decisiontree.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.server.decisiontree.service.DecisionTreeService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class VxmlControllerTest {

    @InjectMocks
    VxmlController vxmlController = new VxmlController();

    @Mock
    private DecisionTreeService decisionTreeService;

    @Mock
    private HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Before
    public void initMocks() {

        MockitoAnnotations.initMocks(this);
     }

    @Test
    public void nodeTest () {

        //TODO - implement properly
        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);

    }

}
