package org.motechproject.server.kookoo.it;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ektorp.CouchDbConnector;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.*;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.decisiontree.service.impl.AllFlowSessionRecords;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Random;
import java.util.UUID;

import static java.lang.String.format;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testKookooContext.xml"})
public class CallFlowIT extends SpringIntegrationTest {

    static Server server;
    static String CONTEXT_PATH = "/motech";

    String KOOKOO_CALLBACK_URL = "/kookoo/ivr";
    String SERVER_URL = "http://localhost:7080" + CONTEXT_PATH + KOOKOO_CALLBACK_URL;

    @Autowired
    AllTrees allTrees;

    @Autowired
    private AllFlowSessionRecords allFlowSessionRecords;

    @Autowired
    @Qualifier("treesDatabase")
    CouchDbConnector connector;

    DefaultHttpClient kookooIvrController;
    Random random;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new Server(7080);
        Context context = new Context(server, CONTEXT_PATH);

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:testKookooContext.xml");

        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");


        server.setHandler(context);
        server.start();
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @Before
    public void setup() {
        setIgnoreWhitespace(true);
        kookooIvrController = new DefaultHttpClient();
        random = new Random(55645474);

    }

    @After
    public void teardown() {
        allTrees.removeAll();
        allFlowSessionRecords.removeAll();
    }

    @Test
    public void shouldPlayPromptsAndRequestDtmfIfNodeHasTransitions() throws Exception {
        Tree tree = new Tree();
        tree.setName("someTree");
        tree.setRootTransition(new Transition().setDestinationNode(
            new Node()
                .addPrompts(new TextToSpeechPrompt().setMessage("hello"))
                .addTransition("1", new Transition().setDestinationNode(new Node()))
                .setTransitionKeyEndMarker("*")
        ));
        allTrees.addOrReplace(tree);

        String response = kookooIvrController.execute(new HttpGet(format("%s?tree=someTree&trP=Lw&ln=en&sid=shouldPlayPromptsAndRequestDtmfIfNodeHasTransitions", SERVER_URL)), new BasicResponseHandler());
        String expectedResponse =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<response>" +
                "   <playtext>hello</playtext>" +
                "   <collectdtmf l=\"1\" t=\"*\" o=\"5000\"></collectdtmf>" +
                "   <gotourl>http://localhost:7080/motech/kookoo/ivr?provider=kookoo&amp;ln=en&amp;tree=someTree&amp;trP=Lw</gotourl>" +
                "</response>";
        assertXMLEqual(expectedResponse, response);
    }

    @Test
    public void shouldTransitionToNextNodeOnDtmfInput() throws Exception {
        Tree tree = new Tree();
        tree.setName("someTree");
        tree.setRootTransition(new Transition().setDestinationNode(
            new Node()
                .addTransition(
                    "1", new Transition().setDestinationNode(new Node()
                    .addPrompts(
                        new TextToSpeechPrompt().setMessage("pressed one"))))
                .addTransition(
                    "2", new Transition().setDestinationNode(new Node()
                        .addPrompts(
                            new TextToSpeechPrompt().setMessage("pressed two"))
                        .addTransition(
                            "*", new Transition())))
        ));
        allTrees.addOrReplace(tree);

        String response = kookooIvrController.execute(new HttpGet(format("%s?tree=someTree&trP=Lw&ln=en&event=GotDTMF&data=2&sid="+UUID.randomUUID().getMostSignificantBits() , SERVER_URL)), new BasicResponseHandler());
        String expectedResponse =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<response>" +
                "   <playtext>pressed two</playtext>" +
                "   <collectdtmf l=\"1\" t=\"#\"  o=\"5000\"></collectdtmf>" +
                "   <gotourl>http://localhost:7080/motech/kookoo/ivr?provider=kookoo&amp;ln=en&amp;tree=someTree&amp;trP=LzI</gotourl>" +
                "</response>";
        assertXMLEqual(expectedResponse, response);
    }

    @Test
    public void shouldHangupIfNodeHasNoTransitions() throws Exception {
        Tree tree = new Tree();
        tree.setName("someTree");
        tree.setRootTransition(new Transition().setDestinationNode(
            new Node().addPrompts(
                    new AudioPrompt().setAudioFileUrl("music.wav"))
        ));
        allTrees.addOrReplace(tree);

        String response = kookooIvrController.execute(new HttpGet(format("%s?tree=someTree&trP=Lw&ln=en&sid=noTransition", SERVER_URL)), new BasicResponseHandler());
        String expectedResponse =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<response>" +
                "   <playaudio>music.wav</playaudio>" +
                "   <hangup></hangup>" +
                "</response>";
        assertXMLEqual(expectedResponse, response);
    }

    @Test
    public void shouldEndTheCallWhenUserHangsup() throws Exception {
        Tree tree = new Tree();
        tree.setName("someTree");
        tree.setRootTransition(new Transition().setDestinationNode(
            new Node().addPrompts(
                new AudioPrompt().setAudioFileUrl("music.wav"))
        ));
        allTrees.addOrReplace(tree);

        String response = kookooIvrController.execute(new HttpGet(format("%s?tree=someTree&trP=aGFuZ3Vw&ln=en&sid=noTransition&event=Hangup", SERVER_URL)), new BasicResponseHandler());
        String expectedResponse =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<response>" +
                "   <hangup></hangup>" +
                "</response>";
        assertXMLEqual(expectedResponse, response);
    }

    @Test
    public void shouldEndTheCallWhenUserDisconnects() throws Exception {
        Tree tree = new Tree();
        tree.setName("someTree");
        tree.setRootTransition(new Transition().setDestinationNode(
            new Node().addPrompts(
                new AudioPrompt().setAudioFileUrl("music.wav"))
        ));
        allTrees.addOrReplace(tree);

        String response = kookooIvrController.execute(new HttpGet(format("%s?tree=someTree&trP=aGFuZ3Vw&ln=en&sid=noTransition&event=Disconnect", SERVER_URL)), new BasicResponseHandler());
        String expectedResponse =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<response>" +
                "   <hangup></hangup>" +
                "</response>";
        assertXMLEqual(expectedResponse, response);
    }

    @Test
    public void shouldRequestMaxLengthDtmfForArbitraryInput() throws Exception {
        Tree tree = new Tree();
        tree.setName("someTree");
        tree.setRootTransition(new Transition().setDestinationNode(
            new Node()
                .addTransition(
                    "?", new Transition())
        ));
        allTrees.addOrReplace(tree);

        String response = kookooIvrController.execute(new HttpGet(format("%s?tree=someTree&trP=Lw&ln=en&sid=shouldRequestMaxLengthDtmfForArbitraryInput", SERVER_URL)), new BasicResponseHandler());
        String expectedResponse =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<response>" +
                "   <collectdtmf l=\"50\" t=\"#\" o=\"5000\"></collectdtmf>" +
                "   <gotourl>http://localhost:7080/motech/kookoo/ivr?provider=kookoo&amp;ln=en&amp;tree=someTree&amp;trP=Lw</gotourl>" +
                "</response>";
        assertXMLEqual(expectedResponse, response);
    }

    @Test
    public void shouldTransitionToNextNodeOnArbitraryInput() throws Exception {
        Tree tree = new Tree();
        tree.setName("someTree");
        tree.setRootTransition(new Transition().setDestinationNode(
            new Node()
                .addTransition(
                    "?", new CustomTransition())
        ));
        allTrees.addOrReplace(tree);

        String response = kookooIvrController.execute(new HttpGet(format("%s?tree=someTree&trP=Lw&ln=en&event=GotDTMF&data=31415&sid="+ UUID.randomUUID().getMostSignificantBits() , SERVER_URL)), new BasicResponseHandler());
        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<response>" +
            "   <playtext>you entered 31415</playtext>" +
            "   <collectdtmf l=\"1\" t=\"#\" o=\"5000\"></collectdtmf>" +
            "   <gotourl>http://localhost:7080/motech/kookoo/ivr?provider=kookoo&amp;ln=en&amp;tree=someTree&amp;trP=LzMxNDE1</gotourl>" +
            "</response>";
        assertXMLEqual(expectedResponse, response);
    }

    public static class CustomTransition implements ITransition {

        @Override
        public Node getDestinationNode(String input, FlowSession session) {
            return new Node()
                .addPrompts(new TextToSpeechPrompt().setMessage(format("you entered %s", input)))
                .addTransition("*", new Transition().setDestinationNode(new Node().setPrompts(new AudioPrompt().setAudioFileUrl("You Pressed Star.wav"))));
        }

        //TODO need end-to-end tests with session.
    }
}