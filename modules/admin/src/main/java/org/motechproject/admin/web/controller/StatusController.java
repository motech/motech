package org.motechproject.admin.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A status controller used mainly for testing purposes.
 */
@Controller
@RequestMapping("web-api")
public class StatusController {

    private static final String OK = "OK";

    /**
     * @return {@code OK} is always the response from this method
     */
    @RequestMapping("/status")
    @ResponseBody
    public String getStatus() {
        return OK;
    }

}
