package org.motechproject.mds.testJdoDiscriminator.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;

import org.motechproject.mds.testJdoDiscriminator.service.HelloWorldService;

/**
 * Controller for HelloWorld message and bundle status.
 */
@Controller
public class HelloWorldController {

    @Autowired
    private HelloWorldService helloWorldService;

    private static final String OK = "OK";

    @RequestMapping("/web-api/status")
    @ResponseBody
    public String status() {
        return OK;
    }

    @RequestMapping("/sayHello")
    @ResponseBody
    public String sayHello() {
        return String.format("{\"message\":\"%s\"}", helloWorldService.sayHello());
    }
}
