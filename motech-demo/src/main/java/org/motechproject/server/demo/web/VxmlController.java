package org.motechproject.server.demo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VxmlController extends MultiActionController
{

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    private ModelAndView hellow(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");

        ModelAndView mav = new ModelAndView();

        mav.setViewName("");
        return mav;
    }
}
