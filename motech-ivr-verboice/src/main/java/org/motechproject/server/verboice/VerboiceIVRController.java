package org.motechproject.server.verboice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/verboice-ivr")
public class VerboiceIVRController {

    private VerboiceIVRService verboiceIVRService;

    @Autowired
    public VerboiceIVRController(VerboiceIVRService verboiceIVRService) {
        this.verboiceIVRService = verboiceIVRService;
    }

    @ResponseBody
    public String handleRequest(HttpServletRequest request) {
        return verboiceIVRService.getHandler().handle(request.getParameterMap());
    }

}
