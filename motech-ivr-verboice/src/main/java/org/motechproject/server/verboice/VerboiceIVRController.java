package org.motechproject.server.verboice;

import org.motechproject.server.verboice.domain.VerboiceResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/verboice-ivr")
public class VerboiceIVRController {
    @ResponseBody
    public String handleRequest(HttpServletRequest request) {
        return new VerboiceResponse(request.getRequestURI()).toXMLString();
    }
}
