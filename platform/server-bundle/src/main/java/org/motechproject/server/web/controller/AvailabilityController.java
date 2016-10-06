package org.motechproject.server.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.motechproject.osgi.web.service.UIFrameworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller class responsible for determining available UI tabs for modules where
 * UI tabs availability depends on different user permissions.
 */
@Controller
@Api(value = "AvailabilityController", description = "Controller class responsible for determining available UI tabs for modules where\n" +
        "UI tabs availability depends on different user permissions.")
public class AvailabilityController {

    @Autowired
    private UIFrameworkService uiFrameworkService;

    @RequestMapping(value = "/available/{moduleName}", method = RequestMethod.GET)
    @ApiOperation(value = "Returns all the available tabs for a given module")
    @ResponseBody
    public List<String> getAvailableTabs(@PathVariable String moduleName) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, List<String>> tabAccessMap = uiFrameworkService.getModuleData(moduleName).getTabAccessMap();

        ArrayList<String> availableTabs = new ArrayList<String>();
        for (String tab : tabAccessMap.keySet()) {
            //If no permissions were specified tab is available for everyone
            if (tabAccessMap.get(tab) == null || tabAccessMap.get(tab).size() == 0) {
                availableTabs.add(tab);
            } else {
                for (String permission : tabAccessMap.get(tab)) {
                    if (auth.getAuthorities().contains(new SimpleGrantedAuthority(permission))) {
                        if (!availableTabs.contains(tab)) {
                            availableTabs.add(tab);
                        }
                    }
                }
            }
        }

        return availableTabs;
    }

}
