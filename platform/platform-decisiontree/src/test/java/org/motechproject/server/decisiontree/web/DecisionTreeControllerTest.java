package org.motechproject.server.decisiontree.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.Action;
import org.motechproject.decisiontree.model.INodeOperation;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.decisiontree.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.decisiontree.service.FlowSessionService;
import org.motechproject.server.decisiontree.service.DecisionTreeService;
import org.motechproject.server.decisiontree.service.TreeEventProcessor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.server.decisiontree.web.DecisionTreeController.LANGUAGE_PARAM;
import static org.motechproject.server.decisiontree.web.DecisionTreeController.NODE_TEMPLATE_NAME;
import static org.motechproject.server.decisiontree.web.DecisionTreeController.TRANSITION_KEY_PARAM;
import static org.motechproject.server.decisiontree.web.DecisionTreeController.TREE_NAME_PARAM;
import static org.motechproject.server.decisiontree.web.DecisionTreeController.TYPE_PARAM;

@RunWith(MockitoJUnitRunner.class)
public class DecisionTreeControllerTest {
    private final String treeName = "treeName";
    private final String patientId = "001";
    private Map<String, Object> params = new HashMap<String, Object>();
    private final String message = "message";

    @InjectMocks
    DecisionTreeController decisionTreeController = new DecisionTreeController();

    @Mock
    private DecisionTreeService decisionTreeService;

    @Mock
    private TreeEventProcessor treeEventProcessor;

    private MockHttpServletRequest request;

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
        request = new MockHttpServletRequest();
        MockitoAnnotations.initMocks(this);
        doNothing().when(autoWireCapableFactory).autowireBean(anyObject());
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(autoWireCapableFactory);
        flowSession = new InMemoryFlowSession();
        when(flowSessionService.getSession(any(HttpServletRequest.class))).thenReturn(flowSession);
    }

    @Test
    public void nodeTest() {
        Node childNode = new Node();
        Transition childTransition = new Transition();
        final MyINodeOperation iNodeOperation = new MyINodeOperation();
        childNode.addOperations(iNodeOperation);
        childTransition.setDestinationNode(new Node());
        childNode.addTransition("1", childTransition);

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(childNode);
        node.addTransition("1", transition);

        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(TYPE_PARAM, "verboice");
        request.setParameter(TRANSITION_KEY_PARAM, "1");
        request.setParameter(DecisionTreeController.FLOW_SESSION_ID_PARAM, "1234");

        flowSession.setCurrentNode(node);
        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
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

        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(TYPE_PARAM, "vxml");
        request.setParameter(TRANSITION_KEY_PARAM, "1");

        flowSession.setCurrentNode(node);
        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        assertEquals("/vm/node-vxml", modelAndView.getViewName());
    }


    @Test
    public void shouldThrowExceptionForInvalidTransition() {
        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(new Node());
        node.addTransition("1", transition);

        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(TRANSITION_KEY_PARAM, "2");
        request.setParameter(TYPE_PARAM, "vxml");

        flowSession.setCurrentNode(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        assertEquals("/vm/error-vxml", modelAndView.getViewName());
        assertEquals(DecisionTreeController.Error.INVALID_TRANSITION_KEY.toString(), modelAndView.getModel().get(message));
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

        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(TRANSITION_KEY_PARAM, transitionKey);
        request.setParameter(TYPE_PARAM, "vxml");

        flowSession.setCurrentNode(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        assertEquals("/vm/error-vxml", modelAndView.getViewName());
    }

    @Test
    public void nodeTestNoInputGoesToBlankTransition() {
        Node childNode = new Node();
        Transition childTransition = new Transition();
        final MyINodeOperation iNodeOperation = new MyINodeOperation();
        childNode.addOperations(iNodeOperation);
        childTransition.setDestinationNode(new Node());
        childNode.addTransition("", childTransition);

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(childNode);
        node.addTransition("", transition);

        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(TYPE_PARAM, "verboice");
        request.setParameter(TRANSITION_KEY_PARAM, "");
        request.setParameter(DecisionTreeController.FLOW_SESSION_ID_PARAM, "1234");


        flowSession.setCurrentNode(node);
        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        assertEquals(NODE_TEMPLATE_NAME + "-" + "verboice", modelAndView.getViewName());
    }

    @Test
    public void nodeTestNoTree() {
        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(TRANSITION_KEY_PARAM, "1");
        request.setParameter(TYPE_PARAM, "vxml");

        flowSession.set(DecisionTreeController.CURRENT_NODE, null);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        assertEquals("/vm/error-vxml", modelAndView.getViewName());
        assertEquals(DecisionTreeController.Error.UNEXPECTED_EXCEPTION.toString(), modelAndView.getModel().get(message));
    }

    @Test
    public void nodeTestException() {
        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(DecisionTreeController.TRANSITION_KEY_PARAM, "1");
        request.setParameter(TYPE_PARAM, "vxml");

        flowSession.set(DecisionTreeController.CURRENT_NODE, null);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        assertEquals("/vm/error-vxml", modelAndView.getViewName());
        assertEquals(DecisionTreeController.Error.UNEXPECTED_EXCEPTION.toString(), modelAndView.getModel().get(message));
    }

    @Test
    public void rootNodeTest() {
        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(new Node());
        node.addTransition("1", transition);

        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(TYPE_PARAM, "vxml");
        flowSession.setCurrentNode(node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        assertEquals(NODE_TEMPLATE_NAME + "-" + "vxml", modelAndView.getViewName());
    }

    @Test
    public void rootNodeTestNoNode() {
        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(TYPE_PARAM, "vxml");
        flowSession.set(DecisionTreeController.CURRENT_NODE, null);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        assertEquals("/vm/error-vxml", modelAndView.getViewName());
        assertEquals(DecisionTreeController.Error.UNEXPECTED_EXCEPTION.toString(), modelAndView.getModel().get(message));
    }

    @Test
    public void rootNodeTestException() {
        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(TYPE_PARAM, "vxml");
        flowSession.set(DecisionTreeController.CURRENT_NODE, null);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        assertEquals("/vm/error-vxml", modelAndView.getViewName());
        assertEquals(DecisionTreeController.Error.UNEXPECTED_EXCEPTION.toString(), modelAndView.getModel().get(message));
    }

    @Test
    public void nodeTestMissingParameters() {
        request.setParameter(TYPE_PARAM, "vxml");

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService, times(0)).getNode(anyString(), anyString(), any(FlowSession.class));
        assertEquals("/vm/error-vxml", modelAndView.getViewName());
        assertEquals(DecisionTreeController.Error.TREE_OR_LANGUAGE_MISSING.toString(), modelAndView.getModel().get(message));
    }

    @Test
    public void nodeTestMissingTransitionPathsParameterReturnsRootNode() {
        Node rootNode = new Node();

        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(TYPE_PARAM, "vxml");
        when(decisionTreeService.getRootNode(eq(treeName), any(FlowSession.class))).thenReturn(rootNode);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        assertNotNull(modelAndView);
        verify(decisionTreeService, times(1)).getRootNode(eq(treeName), any(FlowSession.class));
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

        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(DecisionTreeController.TRANSITION_KEY_PARAM, transitionKey);


        flowSession.setCurrentNode(parentNode);

        //when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class),parentNode);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        params.put(TREE_NAME_PARAM, treeName);
        params.put(LANGUAGE_PARAM, "en");
        params.put(TRANSITION_KEY_PARAM, transitionKey);

        verify(treeEventProcessor).sendActionsBefore(node, params);
        verify(treeEventProcessor, times(0)).sendActionsBefore(parentNode, params);
        verify(treeEventProcessor, times(0)).sendActionsAfter(node, params);
    }

    @Test
    public void sendActionsAfterTest() {
        Node node = new Node();
        String transitionKey = "1";

        Node parentNode = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(node);
        parentNode.addTransition(transitionKey, transition);


        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(DecisionTreeController.TRANSITION_KEY_PARAM, transitionKey);

        flowSession.setCurrentNode(parentNode);

        //when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class),parentNode);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        params.put(TREE_NAME_PARAM, treeName);
        params.put(LANGUAGE_PARAM, "en");
        params.put(TRANSITION_KEY_PARAM, transitionKey);

        verify(treeEventProcessor, times(0)).sendActionsBefore(parentNode, params);
        verify(treeEventProcessor, times(1)).sendActionsAfter(parentNode, params);
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

        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(TYPE_PARAM, "vxml");
        request.setParameter(TRANSITION_KEY_PARAM, "1");

        flowSession.setCurrentNode(node);

        //when(decisionTreeService.getNode(eq(treeName), eq(transitionPath), any(FlowSession.class),node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        params.put(TREE_NAME_PARAM, treeName);
        params.put(LANGUAGE_PARAM, "en");
        params.put(TRANSITION_KEY_PARAM, "1");
        params.put(TYPE_PARAM, "vxml");

        verify(treeEventProcessor).sendTransitionActions(transition, params);
        assertEquals(NODE_TEMPLATE_NAME + "-" + "vxml", modelAndView.getViewName());
    }

    @Test
    public void sendActionsBeforeRootTest() {
        String patientId = "PATIENT_ID";
        Node node = new Node();

        request.setParameter(TREE_NAME_PARAM, treeName);
        request.setParameter(LANGUAGE_PARAM, "en");
        request.setParameter(TYPE_PARAM, "vxml");
        flowSession.setCurrentNode(node);

        //when(decisionTreeService.getNode(eq(treeName), eq(TreeNodeLocator.PATH_DELIMITER), any(FlowSession.class),node);

        ModelAndView modelAndView = decisionTreeController.node(request, response);

        params.put(TREE_NAME_PARAM, treeName);
        params.put(LANGUAGE_PARAM, "en");
        params.put(TYPE_PARAM, "vxml");

        verify(treeEventProcessor).sendActionsBefore(node, params);
        verify(treeEventProcessor, times(0)).sendActionsAfter(node, params);

        // verify(decisionTreeService).getNode(eq(treeName), eq(TreeNodeLocator.PATH_DELIMITER), any(FlowSession.class));
        assertEquals(NODE_TEMPLATE_NAME + "-" + "vxml", modelAndView.getViewName());

    }

    static class InMemoryFlowSession implements FlowSession {

        private String language;
        Map<String, Serializable> store = new HashMap<String, Serializable>();
        private Node currentNode;

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
        public <T extends Serializable> void set(String key, T value) {
            store.put(key, value);
        }

        @Override
        public <T extends Serializable> T get(String key) {
            return (T) store.get(key);
        }

        @Override
        public void setCurrentNode(Node node) {
            this.currentNode = node;
        }

        @Override
        public Node getCurrentNode() {
            return currentNode;  //To change body of implemented methods use File | Settings | File Templates.
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
