package org.motechproject.mds.web.controller;

import org.motechproject.security.domain.MotechUserProfile;
import org.motechproject.security.service.MotechUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private MotechUserService motechUserService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<MotechUserProfile> getUsers() {
        try {
            return motechUserService.getUsers();
        } catch (AccessDeniedException e) {
            LOGGER.debug("Access denied for user list retrieval, returning empty list", e);
            return Collections.emptyList();
        }
    }
}
