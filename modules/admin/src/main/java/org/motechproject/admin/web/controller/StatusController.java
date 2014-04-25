package org.motechproject.admin.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("web-api")
public class StatusController {

    private static final String OK = "OK";

    @RequestMapping("/status")
    @ResponseBody
    public String getStatus() {
        return OK;
    }

}
