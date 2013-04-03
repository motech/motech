package org.motechproject.callflow.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.callflow.domain.FlowSessionRecord;
import org.motechproject.decisiontree.core.DecisionTreeService;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.Action;
import org.motechproject.decisiontree.core.model.INodeOperation;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.event.listener.EventRelay;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class CallFlowServerTest {

    CallFlowServer decisionTreeServer;

    @Mock
    private DecisionTreeService decisionTreeService;
    @Mock
    private TreeEventProcessor treeEventProcessor;
    @Mock
    ApplicationContext applicationContext;
    @Mock
    AutowireCapableBeanFactory autoWireCapableFactory;
    @Mock
    FlowSessionService flowSessionService;
    @Mock
    EventRelay eventRelay;

    private FlowSession flowSession;

    @Before
    public void setup() {
        initMocks(this);

        doNothing().when(autoWireCapableFactory).autowireBean(anyObject());
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(autoWireCapableFactory);

        flowSession = new InMemoryFlowSession("sid", "3443434");
        when(flowSessionService.findOrCreate(anyString(), anyString())).thenReturn(flowSession);

        decisionTreeServer = new CallFlowServerImpl(decisionTreeService, treeEventProcessor, applicationContext, flowSessionService, eventRelay);
    }

    @Test
    public void shouldRenderRootNode() {
        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(new Node());
        node.addTransition("1", transition);

        flowSession.setCurrentNode(node);

        ModelAndView modelAndView = decisionTreeServer.getResponse(flowSession.getSessionId(), "1234567890", "freeivr", "sometree", null, "en");

        assertEquals("/vm/node-freeivr", modelAndView.getViewName());
    }

    @Test
    public void shouldRenderNode() {
        Node childNode = new Node();
        Transition childTransition = new Transition();
        childTransition.setDestinationNode(new Node());
        childNode.addTransition("1", childTransition);

        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(childNode);
        node.addTransition("1", transition);

        flowSession.setCurrentNode(node);
        ModelAndView modelAndView = decisionTreeServer.getResponse(flowSession.getSessionId(), "1234567890", "freeivr", "sometree", "1", "en");

        assertEquals("/vm/node-freeivr", modelAndView.getViewName());
    }

    @Test
    public void shouldExecuteNodeOperation() {
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

        flowSession.setCurrentNode(node);

        decisionTreeServer.getResponse(flowSession.getSessionId(), "1234567890", "freeivr", "sometree", "1", "en");

        assertTrue(iNodeOperation.isCalled());
    }

    @Test
    public void shouldThrowExceptionForInvalidTransition() {
        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(new Node());
        node.addTransition("1", transition);

        flowSession.setCurrentNode(node);

        ModelAndView modelAndView = decisionTreeServer.getResponse(flowSession.getSessionId(), "1234567890", "freeivr", "sometree", "2", "en");

        assertEquals("/vm/error-freeivr", modelAndView.getViewName());
        Assert.assertEquals(CallFlowServer.Error.INVALID_TRANSITION_KEY.toString(), modelAndView.getModel().get("message"));
    }

    @Test
    public void shouldReturnErrorForInvalidTree() {
        flowSession.set(CallFlowServer.CURRENT_NODE_PARAM, null);

        ModelAndView modelAndView = decisionTreeServer.getResponse(flowSession.getSessionId(), "1234567890", "freeivr", null, "1", "en");

        assertEquals("/vm/error-freeivr", modelAndView.getViewName());
        assertEquals(CallFlowServer.Error.TREE_OR_LANGUAGE_MISSING.toString(), modelAndView.getModel().get("message"));
    }

    @Test
    public void sendActionsBeforeTest() {
        Node node = new Node();
        List<Action> actions = new ArrayList<Action>();
        actions.add(new Action());
        node.setActionsBefore(actions);

        Node parentNode = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(node);
        parentNode.addTransition("1", transition);

        flowSession.setCurrentNode(parentNode);

        decisionTreeServer.getResponse(flowSession.getSessionId(), "1234567890", "freeivr", "sometree", "1", "en");

        verify(treeEventProcessor).sendActionsBefore(node, new HashMap<String, Object>());
        verify(treeEventProcessor, times(0)).sendActionsBefore(parentNode, new HashMap<String, Object>());
        verify(treeEventProcessor, times(0)).sendActionsAfter(node, new HashMap<String, Object>());
    }

    @Test
    public void sendActionsAfterTest() {
        Node node = new Node();
        List<Action> actions = new ArrayList<Action>();
        actions.add(new Action());
        node.setActionsAfter(actions);

        Node parentNode = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(node);
        parentNode.addTransition("1", transition);

        flowSession.setCurrentNode(parentNode);

        decisionTreeServer.getResponse(flowSession.getSessionId(), "1234567890", "freeivr", "sometree", "1", "en");

        verify(treeEventProcessor, times(0)).sendActionsBefore(parentNode, new HashMap<String, Object>());
        verify(treeEventProcessor, times(1)).sendActionsAfter(parentNode, new HashMap<String, Object>());
    }

    @Test
    public void sendTransitionActionsTest() {
        Node node = new Node();
        Transition transition = new Transition();
        transition.setDestinationNode(new Node());
        transition.getDestinationNode().setPrompts(new TextToSpeechPrompt().setName("p"));
        node.addTransition("1", transition);

        flowSession.setCurrentNode(node);

        decisionTreeServer.getResponse(flowSession.getSessionId(), "1234567890", "freeivr", "sometree", "1", "en");

        verify(treeEventProcessor).sendTransitionActions(transition, new HashMap<String, Object>());
    }

    @Test
    public void sendActionsBeforeRootTest() {
        Node node = new Node();

        when(decisionTreeService.getRootNode("sometree", flowSession)).thenReturn(node);

        decisionTreeServer.getResponse(flowSession.getSessionId(), "1234567890", "freeivr", "sometree", null, "en");

        verify(treeEventProcessor).sendActionsBefore(node, new HashMap<String, Object>());
        verify(treeEventProcessor, times(0)).sendActionsAfter(node, new HashMap<String, Object>());
    }

    static class InMemoryFlowSession extends FlowSessionRecord {

        private String language;
        Map<String, Serializable> store = new HashMap<String, Serializable>();
        private Node currentNode;

        public InMemoryFlowSession(String sessionId, String phoneNumber) {
            super(sessionId, phoneNumber);
        }

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
        public String getPhoneNumber() {
            return "1234567890";
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
