package org.motechproject.server.verboice.it;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.web.servlet.DispatcherServlet;

abstract public class VerboiceTest extends SpringIntegrationTest{

    static private Server server;
    public static final String CONTEXT_PATH = "/verboice";
    static final String VERBOICE_IVR_URL = "/ivr";
    static final String SERVER_URL = "http://localhost:7080" + CONTEXT_PATH + VERBOICE_IVR_URL;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new Server(7080);
        Context context = new Context(server, CONTEXT_PATH);//new Context(server, "/", Context.SESSIONS);

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath*:META-INF/motech/*.xml");

        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");
        server.setHandler(context);
        server.start();
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
    }
}
