package org.motechproject.security.web.controllers;

import org.motechproject.security.service.PasswordRecoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class OpenIdTokenController {

    private static final Logger LOG = LoggerFactory.getLogger(OpenIdTokenController.class);

    @Autowired
    PasswordRecoveryService recoveryService;

    @RequestMapping(value = "/onetimetoken", method = RequestMethod.GET)
    public void resetView(@RequestParam String token, HttpServletRequest request, HttpServletResponse response) {
        try {
            recoveryService.validateTokenAndLoginUser(token, request, response);
        } catch (IOException e) {
            LOG.debug("Error redirect.");
        }
    }
}
