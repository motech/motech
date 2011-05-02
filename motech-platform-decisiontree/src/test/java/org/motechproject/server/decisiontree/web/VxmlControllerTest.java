package org.motechproject.server.decisiontree.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.server.decisiontree.service.DecisionTreeService;
import org.motechproject.server.decisiontree.service.TreeNodeLocator;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VxmlControllerTest {
	static final String TREE_NAME = "treeName";
	static final String TRANSITION_PATH = "transitionPath";
	static final String PATIENT_ID = "0001";

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

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(TREE_NAME);
        when(request.getParameter(VxmlController.TRANSITION_PATH_PARAM)).thenReturn(TRANSITION_PATH);
        when(decisionTreeService.getNode(TREE_NAME, TRANSITION_PATH)).thenReturn(new Node());

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(TREE_NAME, TRANSITION_PATH);
        assertEquals(VxmlController.MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void nodeTestNoTree() {

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(TREE_NAME);
        when(request.getParameter(VxmlController.TRANSITION_PATH_PARAM)).thenReturn(TRANSITION_PATH);
        when(decisionTreeService.getNode(TREE_NAME, TRANSITION_PATH)).thenReturn(null);

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(TREE_NAME, TRANSITION_PATH);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void nodeTestException () {

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(TREE_NAME);
        when(request.getParameter(VxmlController.TRANSITION_PATH_PARAM)).thenReturn(TRANSITION_PATH);
        when(decisionTreeService.getNode(TREE_NAME, TRANSITION_PATH)).thenThrow(new RuntimeException());

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(TREE_NAME, TRANSITION_PATH);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void rootNodeTest() {

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(TREE_NAME);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn(PATIENT_ID);
        when(decisionTreeService.getNode(TREE_NAME, TreeNodeLocator.PATH_DELIMITER)).thenReturn(new Node());

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(TREE_NAME, TreeNodeLocator.PATH_DELIMITER);
        assertEquals(VxmlController.MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void rootNodeTestNoNode() {

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(TREE_NAME);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn(PATIENT_ID);
        when(decisionTreeService.getNode(TREE_NAME, TRANSITION_PATH)).thenReturn(null);

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(TREE_NAME, TreeNodeLocator.PATH_DELIMITER);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void rootNodeTestException() {
    	
        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(TREE_NAME);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn(PATIENT_ID);
        when(decisionTreeService.getNode(TREE_NAME, TRANSITION_PATH)).thenThrow(new RuntimeException());

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(TREE_NAME, TreeNodeLocator.PATH_DELIMITER);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }
}
