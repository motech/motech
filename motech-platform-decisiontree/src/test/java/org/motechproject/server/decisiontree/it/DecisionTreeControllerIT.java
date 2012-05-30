package org.motechproject.server.decisiontree.it;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ektorp.CouchDbConnector;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.motechproject.decisiontree.model.*;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationDecisionTree.xml"})
public class DecisionTreeControllerIT extends SpringIntegrationTest {

    public static final String AUDIO_FILE_URL = "https://tamaproject.in/tama/wav/stream/en/signature_music.wav";
    public static final String CONTEXT_PATH = "/motech";
    @Autowired
    AllTrees allTrees;

    @Autowired
    @Qualifier("treesDatabase")
    private CouchDbConnector connector;

    static private Server server;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new Server(7080);
        Context context = new Context(server, CONTEXT_PATH);

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:applicationDecisionTree.xml");

        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");
        server.setHandler(context);
        server.start();
    }

    private void createTree() {
        Tree tree = new Tree();
        tree.setName("someTree");
        HashMap<String, Transition> transitions = new HashMap<String, Transition>();
        final Node textToSpeechNode = new Node().addPrompts(new TextToSpeechPrompt().setName("Say this"));
        transitions.put("1", new Transition().setDestinationNode(textToSpeechNode));

        tree.setRootNode(new Node().addPrompts(
            new TextToSpeechPrompt().setMessage("Hello Welcome to motech")
            ,new AudioPrompt().setAudioFileUrl(AUDIO_FILE_URL).setName("audioFile")
        ).setTransitions(transitions));
        allTrees.addOrReplace(tree);
        markForDeletion(tree);
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
    }

    @Before
    public void before() {
        super.before();
        createTree();
    }

    @Test
    public void shouldReturnVXML() throws Exception {
        HttpClient client = new DefaultHttpClient();
        final String vmlUrl = "http://localhost:7080" + CONTEXT_PATH + "/decisiontree/node?type=vxml&pId=asd&ln=en&tree=someTree&trP=Lw==";
        final String response = client.execute(new HttpGet(vmlUrl), new BasicResponseHandler());
        Assert.assertTrue(response.contains("<vxml version=\"2.1\" xmlns=\"http://www.w3.org/2001/vxml\">"));
        Assert.assertTrue(response.contains("<audio src=\""+ AUDIO_FILE_URL + "\">"));

    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }
}
