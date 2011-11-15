package org.motechproject.openmrs.omod.web.servlet;

import org.motechproject.openmrs.omod.service.OmodIdentifierService;
import org.openmrs.api.context.Context;
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
    private static final String ID_GENERATOR = "generator";
    private static final String ID_TYPE = "type";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("Generating id ...");
        String generatedId = getOmodIdentifierService().getIdFor(request.getParameter(ID_GENERATOR), request.getParameter(ID_TYPE));
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.write(generatedId);
        writer.close();
        response.setStatus(HttpServletResponse.SC_OK);
        log.info("New id: " + generatedId);
    }

    protected OmodIdentifierService getOmodIdentifierService() {
        return new OmodIdentifierService();
    }
}
