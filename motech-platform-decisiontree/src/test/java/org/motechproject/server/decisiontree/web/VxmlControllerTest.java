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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

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

        String treeId = "treeId";
        String nodeId = "nodeId";
        String transitionKey = "transitionKey";

        //Node node = new Node();

        when(request.getParameter(VxmlController.TREE_ID_PARAM)).thenReturn(treeId);
        when(request.getParameter(VxmlController.NODE_ID_PARAM)).thenReturn(nodeId);
        when(request.getParameter(VxmlController.TRANSITION_KEY_PARAM)).thenReturn(transitionKey);
        when(decisionTreeService.getNode(treeId, nodeId, transitionKey)).thenReturn(new Node());

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeId, nodeId, transitionKey);
        assertEquals(VxmlController.MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void nodeTestNoTree() {

        String treeId = "treeId";
        String nodeId = "nodeId";
        String transitionKey = "transitionKey";

        //Node node = new Node();

        when(request.getParameter(VxmlController.TREE_ID_PARAM)).thenReturn(treeId);
        when(request.getParameter(VxmlController.NODE_ID_PARAM)).thenReturn(nodeId);
        when(request.getParameter(VxmlController.TRANSITION_KEY_PARAM)).thenReturn(transitionKey);
        when(decisionTreeService.getNode(treeId, nodeId, transitionKey)).thenReturn(null);

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeId, nodeId, transitionKey);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void nodeTestException () {

        String treeId = "treeId";
        String nodeId = "nodeId";
        String transitionKey = "transitionKey";

        //Node node = new Node();

        when(request.getParameter(VxmlController.TREE_ID_PARAM)).thenReturn(treeId);
        when(request.getParameter(VxmlController.NODE_ID_PARAM)).thenReturn(nodeId);
        when(request.getParameter(VxmlController.TRANSITION_KEY_PARAM)).thenReturn(transitionKey);
        when(decisionTreeService.getNode(treeId, nodeId, transitionKey)).thenThrow(new RuntimeException());

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeId, nodeId, transitionKey);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void rootNodeTest() {

        String treeId = "treeId";
        String patientId = "nodeId";


        when(request.getParameter(VxmlController.TREE_ID_PARAM)).thenReturn(treeId);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn(patientId);
        when(decisionTreeService.getNode(treeId,  patientId)).thenReturn(new Node());

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeId, patientId);
        assertEquals(VxmlController.MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void rootNodeTestNoNode() {

        String treeId = "treeId";
        String patientId = "nodeId";


        when(request.getParameter(VxmlController.TREE_ID_PARAM)).thenReturn(treeId);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn(patientId);
        when(decisionTreeService.getNode(treeId,  patientId)).thenReturn(null);

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeId, patientId);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void rootNodeTestException() {

        String treeId = "treeId";
        String patientId = "nodeId";


        when(request.getParameter(VxmlController.TREE_ID_PARAM)).thenReturn(treeId);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn(patientId);
        when(decisionTreeService.getNode(treeId,  patientId)).thenThrow(new RuntimeException());

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeId, patientId);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }
}
