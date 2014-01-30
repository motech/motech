package org.motechproject.security.web.controllers;

import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class for CRUD operations on security rule configuration.
 * Also provides a status method for testing purposes.
 */
@Controller
public class SecurityRulesController {

    private static final String OK = "OK";

    @Autowired
    private MotechURLSecurityService urlSecurityService;

    @RequestMapping(value = "/web-api/securityRules", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateSecurityRules(@RequestBody SecurityConfigDto securityConfig) {
        MotechSecurityConfiguration dbConfig = new MotechSecurityConfiguration();
        dbConfig.setSecurityRules(securityConfig.getSecurityRules());
        urlSecurityService.updateSecurityConfiguration(dbConfig);
    }

    @RequestMapping(value = "/web-api/securityRules", method = RequestMethod.GET)
    @ResponseBody
    public SecurityConfigDto getSecurityRules() {
        SecurityConfigDto security = new SecurityConfigDto();

        List<MotechURLSecurityRule> rules = urlSecurityService.findAllSecurityRules();

            Collections.sort(rules, new Comparator<MotechURLSecurityRule>() {
                @Override
                public int compare(MotechURLSecurityRule o1, MotechURLSecurityRule o2) {
                    int priority1 = o1.getPriority();
                    int priority2 = o2.getPriority();

                    return (priority1 < priority2) ? 1 : -1;
                }
            });
            security.setSecurityRules(rules);

        return security;
    }

    @RequestMapping(value = "/web-api/securityStatus")
    @ResponseBody
    public String securityStatus() {
        return OK;
    }
}
