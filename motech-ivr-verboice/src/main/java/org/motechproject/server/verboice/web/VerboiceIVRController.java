package org.motechproject.server.verboice.web;

import org.motechproject.server.verboice.domain.VerboiceResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class VerboiceIVRController {

    @RequestMapping("/verboice-ivr")
    @ResponseBody
    public String handleRequest(HttpServletRequest request) {
        return new VerboiceResponse(request.getRequestURI()).toXMLString();
    }
}
