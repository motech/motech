package org.motechproject.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.decisiontree.model.*;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.server.decisiontree.DecisionTreeBasedResponseBuilder;
import org.motechproject.server.service.ivr.*;
import org.motechproject.server.service.ivr.IVRSession.IVRCallAttribute;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IvrActionTest {
    @Mock
    private HttpSession httpSession;
    @Mock
    private IVRMessage ivrMessage;
    @Mock
    private TreeChooser treeChooser;
    @Mock
    IVRRequest ivrRequest;
    @Mock
    DecisionTreeBasedResponseBuilder responseBuilder;
    @Mock
    IVRResponseBuilder ivrResponseBuilder;

    private IvrAction tamaIvrAction;

    private IvrActionTest.CommandForTamaIvrActionTest commandForTamaIvrActionTest;

    @Before
    public void setup() {
        initMocks(this);
        when(ivrRequest.getCallerId()).thenReturn("12312");
        when(ivrRequest.getData()).thenReturn("");
        when(ivrRequest.getEvent()).thenReturn(IVREvent.NEW_CALL.name());
        when(responseBuilder.ivrResponse(any(Node.class), any(IVRContext.class), any(KookooIVRResponseBuilder.class), Mockito.anyBoolean())).thenReturn(ivrResponseBuilder);
        when(ivrResponseBuilder.create(ivrMessage, null, "en")).thenReturn("");
        commandForTamaIvrActionTest = new CommandForTamaIvrActionTest();
        when(treeChooser.getTree(any(IVRContext.class))).thenReturn(new TestTreeForTamaIvrActionTest().getTree());
        tamaIvrAction = new IvrAction(treeChooser, ivrMessage, responseBuilder);
    }

    @Test
    public void shouldExecuteCommandIfNextNodeIsNotNull() {
        tamaIvrAction.handle(ivrRequest, new IVRSession(httpSession));
        assertTrue(commandForTamaIvrActionTest.isCalled());
    }

    @Test
    public void shouldExecuteSameCommandIfTransitionIsInvalid() {
        tamaIvrAction.handle(ivrRequest, new IVRSession(httpSession));
        when(httpSession.getAttribute(IVRCallAttribute.CURRENT_DECISION_TREE_POSITION)).thenReturn("/");
        tamaIvrAction.handle(new KookooRequest("sid", "cid", "event", "3"), new IVRSession(httpSession));
        commandForTamaIvrActionTest.setCalled(false);
        assertFalse(commandForTamaIvrActionTest.isCalled());
    }

    @Test
    public void shouldChangeCurrentNodePath() {

        tamaIvrAction.handle(ivrRequest, new IVRSession(httpSession));

        verify(httpSession).setAttribute(IVRSession.IVRCallAttribute.CURRENT_DECISION_TREE_POSITION, "/");
    }

    @Test
    public void shouldNotExecuteCommandIfThereIsNoUserInput() {
        when(httpSession.getAttribute(IVRCallAttribute.CURRENT_DECISION_TREE_POSITION)).thenReturn("/");
        tamaIvrAction.handle(new KookooRequest("sid", "cid", "event", ""), new IVRSession(httpSession));
        assertFalse(commandForTamaIvrActionTest.isCalled());
    }

    class TestTreeForTamaIvrActionTest {

        protected Node createRootNode() {
            Node rootNode = new Node()
                    .setPrompts(new AudioPrompt().setName("foo"))
                    .setTreeCommands(commandForTamaIvrActionTest)
                    .setTransitions(
                            new Object[][]{
                                    {
                                            "1",
                                            new Transition().setDestinationNode(
                                                    new Node().setPrompts(new AudioPrompt().setName("bar")))},
                                    {
                                            "2",
                                            new Transition().setDestinationNode(
                                                           new  Node().setPrompts(new AudioPrompt().setName("baz")))}});
            return rootNode;
        }

        public Tree getTree() {
            return new Tree()
                    .setName(this.getClass().getName())
                    .setRootNode(createRootNode());
        }
    }

    class CommandForTamaIvrActionTest implements ITreeCommand {
        private boolean called;

        @Override
        public String[] execute(Object o) {
            called = true;
            return new String[0];
        }

        public boolean isCalled() {
            return called;
        }

        public void setCalled(boolean called) {
            this.called = called;
        }
    }
}
