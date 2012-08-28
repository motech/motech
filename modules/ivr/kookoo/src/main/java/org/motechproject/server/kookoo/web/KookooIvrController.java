package org.motechproject.server.kookoo.web;

import org.motechproject.decisiontree.core.model.CallStatus;
import org.motechproject.decisiontree.server.service.DecisionTreeServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.String.format;

@Controller
@RequestMapping("/kookoo")
public class KookooIvrController {

    private DecisionTreeServer decisionTreeServer;

    @Autowired
    public KookooIvrController(DecisionTreeServer decisionTreeServer) {
        this.decisionTreeServer = decisionTreeServer;
    }

    @RequestMapping("/ivr")
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response) {
        String transitionKey = null;
        String event = request.getParameter("event");
        if ("GotDTMF".equals(event)) {
            transitionKey = request.getParameter("data");
        } else if("Hangup".equals(event) || "Disconnect".equals(event)) {
            transitionKey = CallStatus.hangup.toString();
        }

        String tree = request.getParameter("tree");
        String sessionId = request.getParameter("sid");
        String language = request.getParameter("ln");
        String phoneNumber = request.getParameter("cid");

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        ModelAndView view = decisionTreeServer.getResponse(sessionId, phoneNumber, "kookoo", tree, transitionKey, language);
        view.addObject("contextPath", request.getContextPath());
        view.addObject("servletPath", request.getServletPath());
        view.addObject("host", request.getHeader("Host"));
        view.addObject("scheme", request.getScheme());
        return view;
    }
}