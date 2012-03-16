package it;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.motechproject.ivr.service.IVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:voxeoResources.xml"})
public class ControllerIt {

    @Autowired
    @Qualifier("VoxeoIVRService")
    private IVRService voxeoIVRService;

    @Test
    @Ignore("run with phone setup at 1234@localhost:5080; config in voxeo-config.json")
    public void shouldInitiateAnOutBoundCall() throws Exception {
        Server server = new Server(8080);
        Context context = new Context(server, "/", Context.SESSIONS);

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:/voxeoResources.xml");

        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");

        server.start();
        server.join();
        Thread.sleep(1000000);
    }

}
