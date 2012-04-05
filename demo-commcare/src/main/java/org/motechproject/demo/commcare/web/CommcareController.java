package org.motechproject.demo.commcare.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CommcareController extends MultiActionController {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    @RequestMapping("/incomingForm")
    public String incomingForm(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("Testing");
    	return null;
    }

    public ModelAndView incoming(HttpServletRequest request, HttpServletResponse response) {
      
        return null;
    }

    public ModelAndView outgoing(HttpServletRequest request, HttpServletResponse response) {
        
        return null;
    }

   
}