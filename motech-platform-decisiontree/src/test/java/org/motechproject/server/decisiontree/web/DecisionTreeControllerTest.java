package org.motechproject.server.decisiontree.web;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.decisiontree.model.*;
import org.motechproject.server.decisiontree.TreeNodeLocator;
import org.motechproject.server.decisiontree.service.DecisionTreeService;
import org.motechproject.server.decisiontree.service.TreeEventProcessor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.motechproject.server.decisiontree.web.DecisionTreeController.*;

@RunWith(MockitoJUnitRunner.class)
public class DecisionTreeControllerTest {
    private final String treeName = "treeName";
    private final String patientId = "001";
    private Map<String, Object> params = new HashMap<String, Object>();
    private final String errorCodeKey = "errorCode";
    private final String transitionPath = "/";

    @InjectMocks
    DecisionTreeController decisionTreeController = new DecisionTreeController();

    @Mock
    private DecisionTreeService decisionTreeService;

    @Mock
    private TreeEventProcessor treeEventProcessor;

    @Mock
    private HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        params.put(TREE_NAME_PARAM, treeName);
    }

    @Test
    public void nodeTest() {
        Node childNode = new Node();
        Transition childTransition = new Transition();
        childTransition.setDestinationNode(new Node());
        childNode.addTransition("1", childTransition);

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(childNode);
        node.addTransition("1", transition);

        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(TYPE_PARAM)).thenReturn("verboice");
        when(request.getParameter(TRANSITION_KEY_PARAM)).thenReturn("1");
        when(request.getParameter(TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, transitionPath);
        assertEquals(NODE_TEMPLATE_NAME + "-" + "verboice", modelAndView.getViewName());
    }

    @Test
    public void leafTest() {

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(new Node());
        Prompt p = new TextToSpeechPrompt();
        p.setName("p");
        transition.getDestinationNode().setPrompts(p);
        node.addTransition("1", transition);


        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(TYPE_PARAM)).thenReturn("vxml");
        when(request.getParameter(TRANSITION_KEY_PARAM)).thenReturn("1");
        when(request.getParameter(TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, transitionPath);
        assertEquals(LEAF_TEMPLATE_NAME + "-" + "vxml", modelAndView.getViewName());
    }


    @Test
    public void nodeTestNoTransitionWithGivenKey() {

        String transitionKey = "1";

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(new Node());
        node.addTransition("2", transition);

        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(TRANSITION_KEY_PARAM)).thenReturn(transitionKey);
        when(request.getParameter(TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, transitionPath);
        assertEquals(ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(Errors.INVALID_TRANSITION_KEY, modelAndView.getModel().get(errorCodeKey));

    }

    @Test
    public void nodeTestInvalidTransitionKeyType() {

        String transitionKey = "1";


        Node destinationNode = new Node();
        Transition transition1 = new Transition();
        transition1.setDestinationNode(new Node());
        destinationNode.addTransition("a", transition1);

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(destinationNode);
        node.addTransition(transitionKey, transition);


        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(TRANSITION_KEY_PARAM)).thenReturn(transitionKey);
        when(request.getParameter(TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, transitionPath);
        assertEquals(ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(Errors.INVALID_TRANSITION_KEY_TYPE, modelAndView.getModel().get(errorCodeKey));

    }

    @Test
    public void nodeTestNoTree() {

        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(TRANSITION_KEY_PARAM)).thenReturn("1");
        when(request.getParameter(TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(null);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, transitionPath);
        assertEquals(DecisionTreeController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(DecisionTreeController.Errors.GET_NODE_ERROR, modelAndView.getModel().get(errorCodeKey));

    }

    @Test
    public void nodeTestException() {

        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(DecisionTreeController.TRANSITION_KEY_PARAM)).thenReturn("1");
        when(request.getParameter(DecisionTreeController.TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));
        when(decisionTreeService.getNode(treeName, transitionPath)).thenThrow(new RuntimeException());

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, transitionPath);
        assertEquals(DecisionTreeController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(DecisionTreeController.Errors.GET_NODE_ERROR, modelAndView.getModel().get(errorCodeKey));

    }

    @Test
    public void rootNodeTest() {

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(new Node());
        node.addTransition("1", transition);

        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(TYPE_PARAM)).thenReturn("vxml");
        when(decisionTreeService.getNode(treeName, TreeNodeLocator.PATH_DELIMITER)).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, TreeNodeLocator.PATH_DELIMITER);
        assertEquals(NODE_TEMPLATE_NAME + "-" + "vxml", modelAndView.getViewName());

    }

    @Test
    public void rootNodeTestNoNode() {

        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(null);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, TreeNodeLocator.PATH_DELIMITER);
        assertEquals(DecisionTreeController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(DecisionTreeController.Errors.GET_NODE_ERROR, modelAndView.getModel().get(errorCodeKey));

    }

    @Test
    public void rootNodeTestException() {

        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(decisionTreeService.getNode(treeName, transitionPath)).thenThrow(new RuntimeException());

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(treeName, TreeNodeLocator.PATH_DELIMITER);
        assertEquals(DecisionTreeController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(DecisionTreeController.Errors.GET_NODE_ERROR, modelAndView.getModel().get(errorCodeKey));

    }

    @Test
    public void nodeTestMissingParameters() {

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService, times(0)).getNode(anyString(), anyString());
        assertEquals(DecisionTreeController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(DecisionTreeController.Errors.NULL_PATIENTID_LANGUAGE_OR_TREENAME_PARAM, modelAndView.getModel().get(errorCodeKey));
    }

    @Test
    public void nodeTestMissingTransitionPathsParameter() {

        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(DecisionTreeController.TRANSITION_KEY_PARAM)).thenReturn("1");


        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService, times(0)).getNode(anyString(), anyString());
        assertEquals(DecisionTreeController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(DecisionTreeController.Errors.NULL_TRANSITION_PATH_PARAM, modelAndView.getModel().get(errorCodeKey));
    }

    @Test
    public void sendActionsBeforeTest() {

        Node node = new Node();
        List<Action> actions = new ArrayList<Action>();
        Action a = new Action();
        actions.add(a);
        node.setActionsBefore(actions);
        String transitionKey = "1";

        Node parentNode = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(node);
        parentNode.addTransition(transitionKey, transition);


        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(DecisionTreeController.TRANSITION_KEY_PARAM)).thenReturn(transitionKey);
        when(request.getParameter(DecisionTreeController.TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(parentNode);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        verify(treeEventProcessor).sendActionsBefore(node, TreeNodeLocator.PATH_DELIMITER + transitionKey, params);
        verify(treeEventProcessor, times(0)).sendActionsBefore(parentNode, TreeNodeLocator.PATH_DELIMITER, params);
        verify(treeEventProcessor, times(0)).sendActionsAfter(node, TreeNodeLocator.PATH_DELIMITER + transitionKey, params);

    }

    @Test
    public void sendActionsAfterTest() {

        Node node = new Node();
        String transitionKey = "1";

        Node parentNode = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(node);
        parentNode.addTransition(transitionKey, transition);


        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(DecisionTreeController.TRANSITION_KEY_PARAM)).thenReturn(transitionKey);
        when(request.getParameter(DecisionTreeController.TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(parentNode);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        verify(treeEventProcessor, times(0)).sendActionsBefore(parentNode, TreeNodeLocator.PATH_DELIMITER, params);
        verify(treeEventProcessor, times(1)).sendActionsAfter(parentNode, TreeNodeLocator.PATH_DELIMITER, params);

    }


    @Test
    public void sendTransitionActionsTest() {

        String patientId = "PATIENT_ID";

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(new Node());
        Prompt p = new TextToSpeechPrompt();
        p.setName("p");
        transition.getDestinationNode().setPrompts(p);
        node.addTransition("1", transition);


        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(TYPE_PARAM)).thenReturn("vxml");
        when(request.getParameter(TRANSITION_KEY_PARAM)).thenReturn("1");
        when(request.getParameter(TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(treeName, transitionPath)).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        verify(treeEventProcessor).sendTransitionActions(transition, params);
        assertEquals(LEAF_TEMPLATE_NAME + "-" + "vxml", modelAndView.getViewName());

    }


    @Test
    public void sendActionsBeforeRootTest() {

        String patientId = "PATIENT_ID";
        Node node = new Node();

        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(TYPE_PARAM)).thenReturn("vxml");
        when(decisionTreeService.getNode(treeName, TreeNodeLocator.PATH_DELIMITER)).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        verify(treeEventProcessor).sendActionsBefore(node, TreeNodeLocator.PATH_DELIMITER, params);
        verify(treeEventProcessor, times(0)).sendActionsAfter(node, TreeNodeLocator.PATH_DELIMITER, params);

        verify(decisionTreeService).getNode(treeName, TreeNodeLocator.PATH_DELIMITER);
        assertEquals(LEAF_TEMPLATE_NAME + "-" + "vxml", modelAndView.getViewName());

    }
}
