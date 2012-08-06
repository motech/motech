package org.motechproject.server.decisiontree.web;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.*;
import org.motechproject.decisiontree.service.FlowSessionService;
import org.motechproject.server.decisiontree.TreeNodeLocator;
import org.motechproject.server.decisiontree.service.DecisionTreeService;
import org.motechproject.server.decisiontree.service.TreeEventProcessor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

    @Mock
    ApplicationContext applicationContext;
    @Mock
    AutowireCapableBeanFactory autoWireCapableFactory;
    @Mock
    FlowSessionService flowSessionService;
    private FlowSession flowSession;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        params.put(TREE_NAME_PARAM, treeName);
        doNothing().when(autoWireCapableFactory).autowireBean(anyObject());
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(autoWireCapableFactory);
        flowSession = new InMemoryFlowSession();
        when(flowSessionService.getSession(any(HttpServletRequest.class))).thenReturn(flowSession);
    }

    @Test
    public void nodeTest() {
        Node childNode = new Node();
        Transition childTransition = new Transition();
        final  MyINodeOperation iNodeOperation = new MyINodeOperation();
        childNode.addOperations(iNodeOperation);
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
        when(request.getParameter(DecisionTreeController.FLOW_SESSION_ID_PARAM)).thenReturn("1234");

        when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class))).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(eq(treeName), eq(transitionPath), any(FlowSession.class));
        assertEquals(NODE_TEMPLATE_NAME + "-" + "verboice", modelAndView.getViewName());
        assertTrue(iNodeOperation.isCalled());
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

        when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class))).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(eq(treeName), eq(transitionPath), any(FlowSession.class));
        assertEquals(NODE_TEMPLATE_NAME + "-" + "vxml", modelAndView.getViewName());
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

        when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class))).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(eq(treeName), eq(transitionPath), any(FlowSession.class));
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

        when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class))).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(eq(treeName), eq(transitionPath), any(FlowSession.class));
        assertEquals(ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(Errors.INVALID_TRANSITION_KEY_TYPE, modelAndView.getModel().get(errorCodeKey));
    }

    @Test
    public void nodeTestNoInputGoesToBlankTransition() {
        Node childNode = new Node();
        Transition childTransition = new Transition();
        final  MyINodeOperation iNodeOperation = new MyINodeOperation();
        childNode.addOperations(iNodeOperation);
        childTransition.setDestinationNode(new Node());
        childNode.addTransition("", childTransition);

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(childNode);
        node.addTransition("", transition);

        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(TYPE_PARAM)).thenReturn("verboice");
        when(request.getParameter(TRANSITION_KEY_PARAM)).thenReturn("");
        when(request.getParameter(TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));
        when(request.getParameter(DecisionTreeController.FLOW_SESSION_ID_PARAM)).thenReturn("1234");

        when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class))).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(eq(treeName), eq(transitionPath), any(FlowSession.class));
        assertEquals(NODE_TEMPLATE_NAME + "-" + "verboice", modelAndView.getViewName());
    }

    @Test
    public void nodeTestNoTree() {
        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(TRANSITION_KEY_PARAM)).thenReturn("1");
        when(request.getParameter(TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

        when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class))).thenReturn(null);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(eq(treeName), eq(transitionPath), any(FlowSession.class));
        assertEquals(DecisionTreeController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(DecisionTreeController.Errors.GET_NODE_ERROR, modelAndView.getModel().get(errorCodeKey));
    }

    @Test
    public void nodeTestException() {
        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(DecisionTreeController.TRANSITION_KEY_PARAM)).thenReturn("1");
        when(request.getParameter(DecisionTreeController.TRANSITION_PATH_PARAM)).thenReturn(Base64.encodeBase64URLSafeString(transitionPath.getBytes()));
        when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class))).thenThrow(new RuntimeException());

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(eq(treeName), eq(transitionPath), any(FlowSession.class));
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
        when(decisionTreeService.getNode(eq(treeName), eq(TreeNodeLocator.PATH_DELIMITER), any(FlowSession.class))).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(eq(treeName), eq(TreeNodeLocator.PATH_DELIMITER), any(FlowSession.class));
        assertEquals(NODE_TEMPLATE_NAME + "-" + "vxml", modelAndView.getViewName());
    }

    @Test
    public void rootNodeTestNoNode() {
        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class))).thenReturn(null);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(eq(treeName), eq(TreeNodeLocator.PATH_DELIMITER), any(FlowSession.class));
        assertEquals(DecisionTreeController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(DecisionTreeController.Errors.GET_NODE_ERROR, modelAndView.getModel().get(errorCodeKey));
    }

    @Test
    public void rootNodeTestException() {
        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class))).thenThrow(new RuntimeException());

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService).getNode(eq(treeName), eq(TreeNodeLocator.PATH_DELIMITER), any(FlowSession.class));
        assertEquals(DecisionTreeController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(DecisionTreeController.Errors.GET_NODE_ERROR, modelAndView.getModel().get(errorCodeKey));
    }

    @Test
    public void nodeTestMissingParameters() {
        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService, times(0)).getNode(anyString(), anyString(), any(FlowSession.class));
        assertEquals(DecisionTreeController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        assertEquals(DecisionTreeController.Errors.NULL_PATIENTID_LANGUAGE_OR_TREENAME_PARAM, modelAndView.getModel().get(errorCodeKey));
    }

    @Test
    public void nodeTestMissingTransitionPathsParameterReturnsRootNode() {
        Node rootNode = new Node();

        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(TYPE_PARAM)).thenReturn("vxml");
        when(decisionTreeService.getNode(eq(treeName), eq("/"), any(FlowSession.class))).thenReturn(rootNode);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService, times(1)).getNode(anyString(), anyString(), any(FlowSession.class));
        assertEquals(NODE_TEMPLATE_NAME + "-" + "vxml", modelAndView.getViewName());
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

        when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class))).thenReturn(parentNode);

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

        when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class))).thenReturn(parentNode);

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

        when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class))).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        verify(treeEventProcessor).sendTransitionActions(transition, params);
        assertEquals(NODE_TEMPLATE_NAME + "-" + "vxml", modelAndView.getViewName());
    }

    @Test
    public void sendActionsBeforeRootTest() {
        String patientId = "PATIENT_ID";
        Node node = new Node();

        when(request.getParameter(TREE_NAME_PARAM)).thenReturn(treeName);
        when(request.getParameter(LANGUAGE_PARAM)).thenReturn("en");
        when(request.getParameter(TYPE_PARAM)).thenReturn("vxml");
        when(decisionTreeService.getNode(eq(treeName), eq(TreeNodeLocator.PATH_DELIMITER), any(FlowSession.class))).thenReturn(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        verify(treeEventProcessor).sendActionsBefore(node, TreeNodeLocator.PATH_DELIMITER, params);
        verify(treeEventProcessor, times(0)).sendActionsAfter(node, TreeNodeLocator.PATH_DELIMITER, params);

        verify(decisionTreeService).getNode(eq(treeName), eq(TreeNodeLocator.PATH_DELIMITER), any(FlowSession.class));
        assertEquals(NODE_TEMPLATE_NAME + "-" + "vxml", modelAndView.getViewName());

    }

    static class InMemoryFlowSession implements FlowSession {

        private String language;
        Map<String, Serializable> store;

        @Override
        public String getSessionId() {
            return "sessionId";
        }

        @Override
        public String getLanguage() {
            return this.language;
        }

        @Override
        public void setLanguage(String language) {
            this.language = language;
        }

        @Override
        public <T extends Serializable> void set(String key,T value) {
            store.put(key,value);
        }

        @Override
        public <T extends Serializable> T get(String key) {
            return null;
        }
    }

    private static class MyINodeOperation implements INodeOperation {
        boolean called;

        @Override
        public void perform(String userInput, FlowSession session) {
            called = true;
        }

        public boolean isCalled() {
            return called;
        }
    }
}
