package org.motechproject.server.verboice.web;

import org.motechproject.server.verboice.VerboiceIVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class VerboiceIVRController {

    private VerboiceIVRService verboiceIVRService;

    @Autowired
    public VerboiceIVRController(VerboiceIVRService verboiceIVRService) {
        this.verboiceIVRService = verboiceIVRService;
    }

    @RequestMapping("/verboice-ivr")
    @ResponseBody
    public String handleRequest(HttpServletRequest request) {
        return verboiceIVRService.getHandler().handle(request.getParameterMap());
    }

}
