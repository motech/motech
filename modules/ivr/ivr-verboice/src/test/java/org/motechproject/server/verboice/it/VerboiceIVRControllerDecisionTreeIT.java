package org.motechproject.server.verboice.it;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.custommonkey.xmlunit.XMLUnit;
import org.ektorp.CouchDbConnector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.*;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testVerboiceContext.xml"})
public class VerboiceIVRControllerDecisionTreeIT extends VerboiceTest {
    private static final String USER_INPUT = "1345234";

    @Autowired
    AllTrees allTrees;

    @Autowired
    VerboiceIVRService verboiceIVRService;

    @Autowired
    @Qualifier("treesDatabase")
    private CouchDbConnector connector;

    private void createTree() {
        Tree tree = new Tree();
        tree.setName("someTree");
        HashMap<String, ITransition> transitions = new HashMap<String, ITransition>();
        final Node textToSpeechNode = new Node().addPrompts(new TextToSpeechPrompt().setMessage("Say this"));
        transitions.put("1", new Transition().setDestinationNode(textToSpeechNode));
        transitions.put("*", new Transition().setDestinationNode(new Node().setPrompts(new AudioPrompt().setAudioFileUrl("you pressed star"))));
        transitions.put("?", new CustomTransition());

        tree.setRootTransition(new Transition().setDestinationNode(new Node().addPrompts(
                new TextToSpeechPrompt().setMessage("Hello Welcome to motech")
        ).setTransitions(transitions)));
        allTrees.addOrReplace(tree);
        markForDeletion(tree);
    }

    @Test
    public void shouldTestVerboiceXMLResponse() throws Exception {
        createTree();

        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", "user123");
        CallRequest callRequest = new CallRequest("phonenumber", params, "someCallBackChannel");
        //verboiceIVRService.initiateCall(callRequest);

        XMLUnit.setIgnoreWhitespace(true);
        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "                        <Say>Hello Welcome to motech</Say>\n" +
                "                                    <Gather method=\"POST\" action=\"http://localhost:7080/motech/verboice/ivr?type=verboice&amp;ln=en&amp;tree=someTree&amp;trP=Lw\" numDigits=\"50\"></Gather>\n" +
                "             </Response>";
        HttpClient client = new DefaultHttpClient();
        String rootUrl = SERVER_URL + "?tree=someTree&motech_call_id="+ callRequest.getCallId() + "&trP=Lw&ln=en";
        String response = client.execute(new HttpGet(rootUrl), new BasicResponseHandler());
        assertXMLEqual(expectedResponse, response);

        String sessionId = UUID.randomUUID().toString();

        String transitionUrl = SERVER_URL + "?tree=someTree&CallSid="+ sessionId + "&trP=Lw&ln=en&Digits=1";
        String response2 = client.execute(new HttpGet(transitionUrl), new BasicResponseHandler());
        assertTrue("got " + response2, response2.contains("<Say>Say this</Say>"));

        String transitionUrl2 = SERVER_URL + "?tree=someTree&CallSid="+ sessionId + "&trP=Lw&ln=en&Digits=*";
        String response3 = client.execute(new HttpGet(transitionUrl2), new BasicResponseHandler());
        assertTrue("got " + response3, response3.contains("<Play>you pressed star</Play>"));

        String transitionUrl3 = SERVER_URL + "?tree=someTree&CallSid="+ sessionId + "&trP=Lw&ln=en&Digits=" + USER_INPUT;
        String response4 = client.execute(new HttpGet(transitionUrl3), new BasicResponseHandler());

        assertTrue("got " + response4, response4.contains("trP=LzEzNDUyMzQ"));   //verify proceeding in tree Lz8 == /?
        assertTrue("got " + response4, response4.contains("<Say>custom transition try 1 with " + USER_INPUT + "</Say>"));
        assertTrue("got " + response4, response4.contains("   <Play>custom_1345234_Hello_from_org.motechproject.server.verboice.it.VerboiceIVRControllerDecisionTreeIT$TestComponent.wav</Play>"));

        String transitionUrl4 = SERVER_URL + "?tree=someTree&CallSid="+ sessionId + "&trP=Lw&ln=en&Digits=" + USER_INPUT;
        String response5 = client.execute(new HttpGet(transitionUrl4), new BasicResponseHandler());
        assertTrue("got " + response5, response5.contains("<Say>custom transition try 2 with " + USER_INPUT + "</Say>"));


        String transitionUrl5 = SERVER_URL + "?tree=someTree&CallSid="+ sessionId + "&trP=LzEzNDUyMzQ&ln=en&Digits=1";
        String response6 = client.execute(new HttpGet(transitionUrl5), new BasicResponseHandler());
        assertTrue("got " + response6, response6.contains(" <Play>option1_after_custom_transition.wav</Play>"));
    }

    @Test
    public void shouldDialAndTestForDialStatus() throws Exception {
        createTreeWithDialPrompt();

        XMLUnit.setIgnoreWhitespace(true);
        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "                        <Dial action=\"http://localhost:7080/motech/verboice/ivr?type=verboice&amp;ln=en&amp;tree=treeWithDial&amp;trP=Lw\">othernumber</Dial>\n" +
                "     </Response>";
        HttpClient client = new DefaultHttpClient();
        String rootUrl = SERVER_URL + "?tree=treeWithDial&trP=Lw&ln=en";
        String response = client.execute(new HttpGet(rootUrl), new BasicResponseHandler());
        assertXMLEqual(expectedResponse, response);

        String transitionUrl = SERVER_URL + "?tree=treeWithDial&trP=Lw&ln=en&DialCallStatus=completed";
        String response2 = client.execute(new HttpGet(transitionUrl), new BasicResponseHandler());
        assertTrue("got " + response2, response2.contains("<Say>Successful Dial</Say>"));
    }

    private void createTreeWithDialPrompt() {
        Tree tree = new Tree();
        tree.setName("treeWithDial");
        HashMap<String, ITransition> transitions = new HashMap<String, ITransition>();
        final Node successTextNode = new Node().addPrompts(new TextToSpeechPrompt().setMessage("Successful Dial"));
        final Node failureTextNode = new Node().addPrompts(new TextToSpeechPrompt().setMessage("Dial Failure"));
        transitions.put("completed", new Transition().setDestinationNode(successTextNode));
        transitions.put("failed", new Transition().setDestinationNode(failureTextNode));

        tree.setRootTransition(new Transition().setDestinationNode(
                new Node().addPrompts(new DialPrompt("othernumber")).setTransitions(transitions)
        ));
        allTrees.addOrReplace(tree);
        markForDeletion(tree);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @Component
    public static class TestComponent {
        String message = "Hello_from_" + this.getClass().getName()+ ".wav";

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
            counter = counter+1;
            session.set("counter", counter);
            transitions.put("1", new Transition().setDestinationNode(new Node().setPrompts(new AudioPrompt().setAudioFileUrl("option1_after_custom_transition.wav"))));
            transitions.put("?", this);
            return new Node().setPrompts(new TextToSpeechPrompt().setMessage("custom transition try " + counter + " with " + input),
                    new AudioPrompt().setAudioFileUrl("custom_" + input + "_" + testComponent.getMessage()))
                    .setTransitions(transitions);
        }
    }
}
