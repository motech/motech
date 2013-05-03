package org.motechproject.server.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class StatusController {

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("web-api/status")
    public String status(){
        return "OK";
    }

}
