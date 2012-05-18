package org.motechproject.sms.http.it;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationSmsHttp.xml"})
public class ControllerIT {
    @Test
    @Ignore("test with telecom provider and app context")
    public void handleIncomingSms() throws Exception {
        Server server = new Server(8080);
        Context context = new Context(server, "/", Context.SESSIONS);

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:/applicationSmsHttp.xml");

        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");

        server.start();
        server.join();
        Thread.sleep(1000000);
    }

}
