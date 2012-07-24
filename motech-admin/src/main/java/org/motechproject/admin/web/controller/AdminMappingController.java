package org.motechproject.admin.web.controller;

import org.motechproject.admin.service.AdminMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class AdminMappingController {

    @Autowired
    private AdminMappingService adminMappingService;

    @RequestMapping(value = "/mappings", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getMappings() {
        return adminMappingService.getAllMappings();
    }
}
