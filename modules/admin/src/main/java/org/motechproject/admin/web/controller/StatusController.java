package org.motechproject.admin.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A status controller used mainly for testing purposes.
 */
@Controller
@Api(value="StatusController", description = "A status controller used mainly for testing purposes")
@RequestMapping("web-api")
public class StatusController {

    private static final String OK = "OK";

    /**
     * @return {@code OK} is always the response from this method
     */
    @RequestMapping("/status")
    @ApiOperation(value="Returns the status 'OK'")
    @ResponseBody
    public String getStatus() {
        return OK;
    }

}
