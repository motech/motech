package org.motechproject.server.voxeo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring MVC controller implementation provides method to handle HTTP requests and generate
 * Appointment Reminder related VXML documents
 */
@Controller
public class CcxmlController {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    @RequestMapping(value = "/ccxml", method = RequestMethod.GET)
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Generate CCXML");

        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");

        String incomingVxml = request.getParameter("incomingVXML");
        String timeout = request.getParameter("timeout");
        String baseUrl = request.getParameter("baseUrl");
        String callTimeout = request.getParameter("callTimeout");

        ModelAndView mav = new ModelAndView();

        mav.setViewName("ccxml");

        mav.addObject("incomingVXML", incomingVxml);
        mav.addObject("timeout", timeout);
        mav.addObject("baseUrl", baseUrl);
        mav.addObject("callTimeout", callTimeout);

        return mav;
    }
}
