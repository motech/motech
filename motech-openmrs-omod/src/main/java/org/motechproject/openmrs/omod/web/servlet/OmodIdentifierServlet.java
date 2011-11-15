package org.motechproject.openmrs.omod.web.servlet;

import org.motechproject.openmrs.omod.service.OmodIdentifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class OmodIdentifierServlet extends HttpServlet {
    private Logger log = LoggerFactory.getLogger(OmodIdentifierServlet.class);
    private OmodIdentifierService omodIdentifierService;

    public OmodIdentifierServlet() {
          omodIdentifierService = new OmodIdentifierService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/xml");
        PrintWriter writer = resp.getWriter();
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ids><id></id></ids>");
        writer.close();
        log.info("called servlet");
    }
}
