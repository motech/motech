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

/**
 * Controller used for OpenId
 */
@Controller
public class OpenIdTokenController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenIdTokenController.class);

    private PasswordRecoveryService recoveryService;

    /**
     * Validates token against one generated to reset password and if
     * it's correct redirects user to home page, otherwise redirects him
     * to login page
     *
     * @param token token that should be used to reset password
     */
    @RequestMapping(value = "/forgotonetimetoken", method = RequestMethod.GET)
    public void resetView(@RequestParam String token, HttpServletRequest request, HttpServletResponse response) {
        try {
            recoveryService.validateTokenAndLoginUser(token, request, response);
        } catch (IOException e) {
            LOGGER.debug("Error redirect.");
        }
    }

    @Autowired
    public void setRecoveryService(PasswordRecoveryService recoveryService) {
        this.recoveryService = recoveryService;
    }
}
