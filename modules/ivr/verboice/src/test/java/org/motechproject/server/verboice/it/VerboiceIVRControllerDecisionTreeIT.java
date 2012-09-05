package org.motechproject.server.verboice.it;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.custommonkey.xmlunit.XMLUnit;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.server.service.impl.AllFlowSessionRecords;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.TreeNodeLocator;
import org.motechproject.decisiontree.core.model.AudioPrompt;
import org.motechproject.decisiontree.core.model.DialPrompt;
import org.motechproject.decisiontree.core.model.DialStatus;
import org.motechproject.decisiontree.core.model.ITransition;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.decisiontree.core.repository.AllTrees;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class VerboiceIVRControllerDecisionTreeIT extends VerboiceTest {
    private static final String USER_INPUT = "1345234";

    @Autowired
    AllTrees allTrees;

    @Autowired
    private AllFlowSessionRecords allFlowSessionRecords;

    @Autowired
    VerboiceIVRService verboiceIVRService;

    @Autowired
    @Qualifier("treesDatabase")
    private CouchDbConnector connector;

    @Before
    public void setUp() throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
    }

    private Tree createTree() {
        Tree tree = new Tree();
        tree.setName("someTree");
        HashMap<String, ITransition> transitions = new HashMap<String, ITransition>();
        final Node textToSpeechNode = new Node().addPrompts(new TextToSpeechPrompt().setMessage("Say this"));

        transitions.put("1", new Transition().setDestinationNode(textToSpeechNode));
        transitions.put("*", new Transition().setDestinationNode(new Node().setPrompts(new AudioPrompt().setAudioFileUrl("you pressed star"))));
        transitions.put("?", new CustomTransition());

        Node rootNode = new Node()
                .addPrompts(new TextToSpeechPrompt().setMessage("Hello Welcome to motech"))
                .setTransitions(transitions)
                .setMaxTransitionInputDigit(10)
                .setMaxTransitionTimeout(2000);


        tree.setRootTransition(new Transition().setDestinationNode(rootNode));
        allTrees.addOrReplace(tree);
        markForDeletion(tree);
        return tree;
    }

    @Test
    public void shouldTestVerboiceXMLResponse() throws Exception {
        createTree();

        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", "user123");
        CallRequest callRequest = new CallRequest("phonenumber", params, "someCallBackChannel");
        //verboiceIVRService.initiateCall(callRequest);

        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "  <Gather method=\"POST\" action=\"http://localhost:7080/motech/verboice/ivr?provider=verboice&amp;ln=en&amp;tree=someTree\" numDigits=\"10\" timeout=\"2\" finishOnKey=\"#\">" +
                "    <Say>Hello Welcome to motech</Say>\n" +
                "  </Gather>\n" +
                "</Response>";
        HttpClient verboiceIvrController = new DefaultHttpClient();
        String rootUrl = SERVER_URL + "?tree=someTree&ln=en&motech_call_id=" + callRequest.getCallId();
        String response = verboiceIvrController.execute(new HttpGet(rootUrl), new BasicResponseHandler());
        assertXMLEqual(expectedResponse, response);

        String sessionId = UUID.randomUUID().toString();

        String transitionUrl = SERVER_URL + "?tree=someTree&CallSid=" + sessionId + "&ln=en&Digits=1";
        String response2 = verboiceIvrController.execute(new HttpGet(transitionUrl), new BasicResponseHandler());
        assertTrue("got " + response2, response2.contains("<Say>Say this</Say>"));

        String transitionUrl2 = SERVER_URL + "?tree=someTree&CallSid=" + UUID.randomUUID().toString() + "&ln=en&Digits=*";
        String response3 = verboiceIvrController.execute(new HttpGet(transitionUrl2), new BasicResponseHandler());
        assertTrue("got " + response3, response3.contains("<Play>you pressed star</Play>"));

        sessionId = UUID.randomUUID().toString();

        String transitionUrl3 = SERVER_URL + "?tree=someTree&CallSid=" + sessionId + "&ln=en&Digits=" + USER_INPUT;
        String response4 = verboiceIvrController.execute(new HttpGet(transitionUrl3), new BasicResponseHandler());

        assertTrue("got " + response4, response4.contains("<Say>custom transition try 1 with " + USER_INPUT + "</Say>"));
        assertTrue("got " + response4, response4.contains("   <Play>custom_1345234_Hello_from_org.motechproject.server.verboice.it.VerboiceIVRControllerDecisionTreeIT$TestComponent.wav</Play>"));

        String transitionUrl4 = SERVER_URL + "?tree=someTree&CallSid=" + sessionId + "&ln=en&Digits=" + USER_INPUT;
        String response5 = verboiceIvrController.execute(new HttpGet(transitionUrl4), new BasicResponseHandler());
        assertTrue("got " + response5, response5.contains("<Say>custom transition try 2 with " + USER_INPUT + "</Say>"));


        String transitionUrl5 = SERVER_URL + "?tree=someTree&CallSid=" + sessionId + "&ln=en&Digits=1";
        String response6 = verboiceIvrController.execute(new HttpGet(transitionUrl5), new BasicResponseHandler());
        assertTrue("got " + response6, response6.contains(" <Play>option1_after_custom_transition.wav</Play>"));
    }

    @Test
    public void shouldDialAndTestForDialStatus() throws Exception {
        createTreeWithDialPrompt();

        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "  <Say>Some Message</Say>\n" +
                "  <Dial callerId=\"callerNumber\" action=\"http://localhost:7080/motech/verboice/ivr?provider=verboice&amp;ln=en&amp;tree=treeWithDial\">othernumber</Dial>\n" +
                "</Response>";
        HttpClient client = new DefaultHttpClient();
        String rootUrl = SERVER_URL + "?tree=treeWithDial&ln=en";
        String response = client.execute(new HttpGet(rootUrl), new BasicResponseHandler());
        assertXMLEqual(expectedResponse, response);

        String transitionUrl = SERVER_URL + "?tree=treeWithDial&ln=en&DialCallStatus=completed";
        String response2 = client.execute(new HttpGet(transitionUrl), new BasicResponseHandler());
        assertTrue("got " + response2, response2.contains("<Say>Successful Dial</Say>"));
    }

    @Test
    public void shouldRedirectOnNoInputTransitionIfSuchTransitionExists() throws Exception {
        createTreeWithNoInputRedirect();

        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "  <Gather method=\"POST\" action=\"http://localhost:7080/motech/verboice/ivr?provider=verboice&amp;ln=en&amp;tree=treeWithNoInputRedirect\" numDigits=\"1\" timeout=\"5\" finishOnKey=\"\">\n" +
                "    <Say>Welcome to motech</Say>\n" +
                "  </Gather>\n" +
                "  <Redirect method=\"POST\">http://localhost:7080/motech/verboice/ivr?provider=verboice&amp;ln=en&amp;tree=treeWithNoInputRedirect&amp;Digits=</Redirect>" +
                "</Response>";
        HttpClient client = new DefaultHttpClient();
        String rootUrl = SERVER_URL + "?tree=treeWithNoInputRedirect&ln=en";
        String response = client.execute(new HttpGet(rootUrl), new BasicResponseHandler());
        assertXMLEqual(expectedResponse, response);

        expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "    <Say>timed out</Say>\n" +
                "</Response>";

        String noInputTransition = SERVER_URL + "?provider=verboice&ln=en&tree=treeWithNoInputRedirect&Digits=";
        response = client.execute(new HttpGet(noInputTransition), new BasicResponseHandler());
        assertXMLEqual(expectedResponse, response);
    }

    @Test
    public void shouldPlayNoticePromptsBeforeTransitionMenu() throws Exception {
        createTreeWithNoticePrompts();

        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "  <Say>Please listen carefully</Say>\n" +
                "  <Say>You wont be able to give any input while this is playing</Say>\n" +
                "  <Gather method=\"POST\" action=\"http://localhost:7080/motech/verboice/ivr?provider=verboice&amp;ln=en&amp;tree=someTree\" numDigits=\"10\" timeout=\"2\" finishOnKey=\"#\">" +
                "    <Say>Hello Welcome to motech</Say>\n" +
                "  </Gather>\n" +
                "</Response>";

        HttpClient verboiceIvrController = new DefaultHttpClient();
        String rootUrl = SERVER_URL + "?tree=someTree" + "&ln=en";
        String response = verboiceIvrController.execute(new HttpGet(rootUrl), new BasicResponseHandler());
        System.out.println(response);
        assertXMLEqual(expectedResponse, response);

    }

    private Tree createTreeWithNoticePrompts() {
        Tree tree = createTree();
        tree.getRootTransition().getDestinationNode(null, null).addNoticePrompts(
                new TextToSpeechPrompt().setMessage("Please listen carefully"),
                new TextToSpeechPrompt().setMessage("You wont be able to give any input while this is playing")
        );
        allTrees.update(tree);
        markForDeletion(tree);
        return tree;
    }

    private Tree createTreeWithNoInputRedirect() {
        Tree tree = new Tree();
        tree.setName("treeWithNoInputRedirect");
        HashMap<String, ITransition> transitions = new HashMap<String, ITransition>();
        transitions.put("1", new Transition().setDestinationNode(new Node().setPrompts(new TextToSpeechPrompt().setMessage("Press One"))));
        transitions.put(TreeNodeLocator.NO_INPUT, new Transition().setDestinationNode(new Node().setPrompts(new TextToSpeechPrompt().setMessage("timed out"))));

        tree.setRootTransition(new Transition().setDestinationNode(
                new Node()
                        .addPrompts(new TextToSpeechPrompt().setMessage("Welcome to motech"))
                        .setTransitions(transitions)
                        .setTransitionKeyEndMarker("")
        ));
        allTrees.addOrReplace(tree);
        markForDeletion(tree);
        return tree;
    }

    private Tree createTreeWithDialPrompt() {
        Tree tree = new Tree();
        tree.setName("treeWithDial");
        HashMap<String, ITransition> transitions = new HashMap<String, ITransition>();
        final Node successTextNode = new Node().addPrompts(new TextToSpeechPrompt().setMessage("Successful Dial"));
        final Node failureTextNode = new Node().addPrompts(new TextToSpeechPrompt().setMessage("Dial Failure"));
        transitions.put(DialStatus.completed.toString(), new Transition().setDestinationNode(successTextNode));
        transitions.put(DialStatus.failed.toString(), new Transition().setDestinationNode(failureTextNode));

        tree.setRootTransition(new Transition().setDestinationNode(
                new Node().addPrompts(
                        new TextToSpeechPrompt().setMessage("Some Message"),
                        new DialPrompt("othernumber").setCallerId("callerNumber")
                ).setTransitions(transitions)
        ));
        allTrees.addOrReplace(tree);
        markForDeletion(tree);
        return tree;
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void teardown() {
        allFlowSessionRecords.removeAll();
    }

    @Component
    public static class TestComponent {
        String message = "Hello_from_" + this.getClass().getName() + ".wav";

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class CustomTransition implements ITransition {

        @Autowired
        TestComponent testComponent;

        @Override
        public Node getDestinationNode(String input, FlowSession session) {
            final HashMap<String, ITransition> transitions = new HashMap<String, ITransition>();
            Integer counter = session.get("counter");
            if (counter == null) counter = 0;
            counter = counter + 1;
            session.set("counter", counter);
            transitions.put("1", new Transition().setDestinationNode(new Node().setPrompts(new AudioPrompt().setAudioFileUrl("option1_after_custom_transition.wav"))));
            transitions.put("?", this);
            return new Node().setPrompts(new TextToSpeechPrompt().setMessage("custom transition try " + counter + " with " + input),
                    new AudioPrompt().setAudioFileUrl("custom_" + input + "_" + testComponent.getMessage()))
                    .setTransitions(transitions);
        }
    }
}
