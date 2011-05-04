package org.motechproject.server.decisiontree.web;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
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
	private final String treeName = "treeName";
    private final String patientId = "pID";
	private final String transitionPath = Base64.encodeBase64URLSafeString("/".getBytes());

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
    public void nodeTest() {

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(new Node());
        node.addTransition("1", transition);


        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn("PATIENT_ID");
        when(request.getParameter(VxmlController.LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(VxmlController.TRANSITION_KEY_PARAM)).thenReturn("1");
        when(request.getParameter(VxmlController.TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(node);

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, transitionPath);
        assertEquals(VxmlController.MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void nodeTestNoTransitionWithGivenKey() {

        String transitionKey = "1";

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(new Node());
        node.addTransition("2", transition);

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn("PATIENT_ID");
        when(request.getParameter(VxmlController.LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(VxmlController.TRANSITION_KEY_PARAM)).thenReturn(transitionKey);
        when(request.getParameter(VxmlController.TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(node);

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, transitionPath);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void nodeTestInvalidTransitionKey() {

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(new Node());
        node.addTransition("a", transition);

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn("PATIENT_ID");
        when(request.getParameter(VxmlController.LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(VxmlController.TRANSITION_KEY_PARAM)).thenReturn("1");
        when(request.getParameter(VxmlController.TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(node);

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, transitionPath);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void nodeTestInvalidTransitionNoDestinationNode() {

        Node node = new Node();
        Transition transition = new Transition();
        node.addTransition("1", transition);

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn("PATIENT_ID");
        when(request.getParameter(VxmlController.LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(VxmlController.TRANSITION_KEY_PARAM)).thenReturn("1");
        when(request.getParameter(VxmlController.TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(new Node());

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, transitionPath);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void nodeTestNoTree() {

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn("PATIENT_ID");
        when(request.getParameter(VxmlController.LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(VxmlController.TRANSITION_KEY_PARAM)).thenReturn("1");
        when(request.getParameter(VxmlController.TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(null);

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, transitionPath);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void nodeTestException () {

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn("PATIENT_ID");
        when(request.getParameter(VxmlController.LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(VxmlController.TRANSITION_KEY_PARAM)).thenReturn("1");
        when(request.getParameter(VxmlController.TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));
        when(decisionTreeService.getNode(treeName, transitionPath)).thenThrow(new RuntimeException());

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, transitionPath);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void rootNodeTest() {

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn("PATIENT_ID");
        when(request.getParameter(VxmlController.LANGUAGE_PARAM)).thenReturn("en");
        when(decisionTreeService.getNode(treeName, TreeNodeLocator.PATH_DELIMITER)).thenReturn(new Node());

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, TreeNodeLocator.PATH_DELIMITER);
        assertEquals(VxmlController.MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void rootNodeTestNoNode() {

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn("PATIENT_ID");
        when(request.getParameter(VxmlController.LANGUAGE_PARAM)).thenReturn("en");
        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(null);

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, TreeNodeLocator.PATH_DELIMITER);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void rootNodeTestException() {
    	
        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn("PATIENT_ID");
        when(request.getParameter(VxmlController.LANGUAGE_PARAM)).thenReturn("en");
        when(decisionTreeService.getNode(treeName, transitionPath)).thenThrow(new RuntimeException());

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, TreeNodeLocator.PATH_DELIMITER);
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void nodeTestMissingParameters() {

        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService,times(0)).getNode(anyString(), anyString());
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
    }

    @Test
    public void nodeTestMissingTransitionPathsParameter() {

        when(request.getParameter(VxmlController.TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(VxmlController.PATIENT_ID_PARAM)).thenReturn("PATIENT_ID");
        when(request.getParameter(VxmlController.LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(VxmlController.TRANSITION_KEY_PARAM)).thenReturn("1");


        ModelAndView modelAndView = vxmlController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService, times(0)).getNode(anyString(), anyString());
        assertEquals(VxmlController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
    }
}
