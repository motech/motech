package org.motechproject.server.web.controller;

import org.motechproject.server.startup.MotechPlatformState;
import org.motechproject.server.startup.StartupManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    private StartupManager startupManager = StartupManager.getInstance();

    @RequestMapping(value = { "/", "/home", "/index" }, method = RequestMethod.GET)
    public String home() {
        if (startupManager.getPlatformState() == MotechPlatformState.NEED_CONFIG) {
            return "redirect:startup";
        } else {
            return "home";
        }
    }
    
}
