package org.motechproject.security.web.controllers;

import org.motechproject.security.config.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles HTTP requests to retrieve Platform Settings
 */
@Controller
public class SettingsController {

    @Autowired
    private SettingService settingService;

    @RequestMapping(value = "/emailRequired", method = RequestMethod.GET)
    @ResponseBody
    public boolean isEmailRequired() {
        return settingService.getEmailRequired();
    }
}
