package org.motechproject.security.web.controllers;

import org.motechproject.security.model.SecurityConfigDto;
import org.motechproject.security.model.SecurityRuleDto;
import org.motechproject.security.service.MotechURLSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Class for CRUD operations on security rule configuration.
 * Also provides a status method for testing purposes.
 */
@Controller
public class SecurityRulesController {
    private static final String OK = "OK";

    private MotechURLSecurityService urlSecurityService;

    @RequestMapping(value = "/web-api/securityRules", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateSecurityRules(@RequestBody SecurityConfigDto securityConfig) {
        urlSecurityService.updateSecurityConfiguration(securityConfig);
    }

    @RequestMapping(value = "/web-api/securityRules", method = RequestMethod.GET)
    @ResponseBody
    public SecurityConfigDto getSecurityRules() {
        SecurityConfigDto security = new SecurityConfigDto();
        List<SecurityRuleDto> rules = urlSecurityService.findAllSecurityRules();
        security.setSecurityRules(rules);

        return security;
    }

    @RequestMapping(value = "/web-api/securityStatus")
    @ResponseBody
    public String securityStatus() {
        return OK;
    }

    @Autowired
    public void setUrlSecurityService(MotechURLSecurityService urlSecurityService) {
        this.urlSecurityService = urlSecurityService;
    }
}
