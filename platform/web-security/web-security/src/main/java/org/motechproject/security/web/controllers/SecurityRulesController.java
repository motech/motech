package org.motechproject.security.web.controllers;

import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.model.SecurityConfigDto;
import org.motechproject.security.service.MotechURLSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Class for CRUD operations on security rule configuration.
 * Also provides a status method for testing purposes.
 *
 */
@Controller
public class SecurityRulesController {

    private static final String OK = "OK";

    @Autowired
    private MotechURLSecurityService urlSecurityService;

    @RequestMapping(value = "/web-api/updateSecurityRules", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateSecurityRules(@RequestBody SecurityConfigDto securityConfig) {
        MotechSecurityConfiguration dbConfig = new MotechSecurityConfiguration();
        dbConfig.setSecurityRules(securityConfig.getSecurityRules());
        urlSecurityService.updateSecurityConfiguration(dbConfig);
    }

    @RequestMapping(value = "/web-api/securityStatus")
    @ResponseBody
    public String securityStatus() {
        return OK;
    }
}
