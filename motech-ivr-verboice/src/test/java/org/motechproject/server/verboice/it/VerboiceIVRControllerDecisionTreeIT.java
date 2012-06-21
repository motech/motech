package org.motechproject.server.verboice.it;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.custommonkey.xmlunit.XMLUnit;
import org.ektorp.CouchDbConnector;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.motechproject.decisiontree.model.*;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.HashMap;

import static junit.framework.Assert.assertTrue;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testVerboiceContext.xml"})
public class VerboiceIVRControllerDecisionTreeIT extends SpringIntegrationTest {
    static private Server server;
    public static final String CONTEXT_PATH = "/motech";
    private static final String VERBOICE_URL = "/verboice/ivr";
    private static final String SERVER_URL = "http://localhost:7080" + CONTEXT_PATH + VERBOICE_URL;
    private static final String USER_INPUT = "1345234";

    @Autowired
    AllTrees allTrees;

    @Autowired
    @Qualifier("treesDatabase")
    private CouchDbConnector connector;


    @BeforeClass
    public static void startServer() throws Exception {


        server = new Server(7080);
        Context context = new Context(server, CONTEXT_PATH);//new Context(server, "/", Context.SESSIONS);

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:testVerboiceContext.xml");

        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");
        server.setHandler(context);
        server.start();
    }

    @Before
    public void setup() {
        createTree();
    }

    private void createTree() {
        Tree tree = new Tree();
        tree.setName("someTree");
        HashMap<String, ITransition> transitions = new HashMap<String, ITransition>();
        final Node textToSpeechNode = new Node().addPrompts(new TextToSpeechPrompt().setMessage("Say this"));
        transitions.put("1", new Transition().setDestinationNode(textToSpeechNode));
        transitions.put("*", new Transition().setDestinationNode(new Node().setPrompts(new AudioPrompt().setAudioFileUrl("you pressed star"))));
        transitions.put("?", new CustomTransition());

        tree.setRootNode(new Node().addPrompts(
                new TextToSpeechPrompt().setMessage("Hello Welcome to motech")
                //,new AudioPrompt().setAudioFileUrl("https://tamaproject.in/tama/wav/stream/en/signature_music.wav").setName("audioFile")
        ).setTransitions(transitions));
        allTrees.addOrReplace(tree);
        markForDeletion(tree);
    }


    @Test
    public void shouldTestVerboiceXMLResponse() throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "                        <Say>Hello Welcome to motech</Say>\n" +
                "                                    <Gather method=\"POST\" action=\"http://localhost:7080/motech/verboice/ivr?type=verboice&amp;ln=en&amp;tree=someTree&amp;trP=Lw\" numDigits=\"50\"></Gather>\n" +
                "                    <Gather method=\"POST\" action=\"http://localhost:7080/motech/verboice/ivr?type=verboice&amp;ln=en&amp;tree=someTree&amp;trP=Lw\" numDigits=\"50\"></Gather>\n" +
                "                    <Gather method=\"POST\" action=\"http://localhost:7080/motech/verboice/ivr?type=verboice&amp;ln=en&amp;tree=someTree&amp;trP=Lw\" numDigits=\"50\"></Gather>\n" +
                "             </Response>";
        HttpClient client = new DefaultHttpClient();
        String rootUrl = SERVER_URL + "?tree=someTree&trP=Lw&ln=en";
        String response = client.execute(new HttpGet(rootUrl), new BasicResponseHandler());
        assertXMLEqual(expectedResponse, response);

        String transitionUrl = SERVER_URL + "?tree=someTree&trP=Lw&ln=en&Digits=1";
        String response2 = client.execute(new HttpGet(transitionUrl), new BasicResponseHandler());
        assertTrue("got " + response2, response2.contains("<Say>Say this</Say>"));

        String transitionUrl2 = SERVER_URL + "?tree=someTree&trP=Lw&ln=en&Digits=*";
        String response3 = client.execute(new HttpGet(transitionUrl2), new BasicResponseHandler());
        assertTrue("got " + response3, response3.contains("<Play>you pressed star</Play>"));


        String transitionUrl3 = SERVER_URL + "?tree=someTree&trP=Lw&ln=en&Digits=" + USER_INPUT;
        String response4 = client.execute(new HttpGet(transitionUrl3), new BasicResponseHandler());

        assertTrue("got " + response4, response4.contains("trP=Lz8"));   //verify proceeding in tree Lz8 == /?
        assertTrue("got " + response4, response4.contains("<Say>custom transition " + USER_INPUT + "</Say>"));
        assertTrue("got " + response4, response4.contains("   <Play>custom_1345234_Hello_from_org.motechproject.server.verboice.it.VerboiceIVRControllerDecisionTreeIT$TestComponent.wav</Play>"));
    }

    @Test
    public void shouldReturnVerboiceML() throws Exception {
        HttpClient client = new DefaultHttpClient();
        final String vmlUrl = SERVER_URL + "?tree=someTree&type=verboice&pId=asd&ln=en&tNm=someTree&trP=Lw==";
        final String response = client.execute(new HttpGet(vmlUrl), new BasicResponseHandler());
        Assert.assertTrue(response.contains("<Response>"));
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
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
        public Node getDestinationNode(String input) {
            final HashMap<String, ITransition> transitions = new HashMap<String, ITransition>();
            transitions.put("1", new Transition().setDestinationNode(new Node().setPrompts(new AudioPrompt().setAudioFileUrl("option1_after_custom_transition.wav"))));
            transitions.put("?", this);
            return new Node().setPrompts(new TextToSpeechPrompt().setMessage("custom transition " + input),
                    new AudioPrompt().setAudioFileUrl("custom_" + input + "_" + testComponent.getMessage()))
                    .setTransitions(transitions);
        }
    }

}
