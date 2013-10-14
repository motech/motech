package org.motechproject.security.web.controllers;

import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.service.MotechPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PermissionController {

    @Autowired
    private MotechPermissionService motechPermissionService;

    @RequestMapping(value = "/permissions", method = RequestMethod.GET)
    @ResponseBody
    public List<PermissionDto> getPermissions() {
        return motechPermissionService.getPermissions();
    }
}
