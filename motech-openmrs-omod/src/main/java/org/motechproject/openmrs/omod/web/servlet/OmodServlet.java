package org.motechproject.openmrs.omod.web.servlet;

import org.motechproject.openmrs.omod.service.OmodIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class OmodServlet extends HttpServlet {

    private Logger log = LoggerFactory.getLogger(OmodServlet.class);

    private OmodIdService omodIDService = new OmodIdService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/xml");
        PrintWriter writer = resp.getWriter();
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ids><id>" + omodIDService.generateFacilityId() + "</id></ids>");
        writer.close();
        log.info("called servlet");
    }
}
