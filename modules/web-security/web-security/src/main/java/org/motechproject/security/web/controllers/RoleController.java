package org.motechproject.security.web.controllers;

import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class RoleController {

    @Autowired
    private MotechRoleService motechRoleService;

    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    @ResponseBody
    public List<RoleDto> getRoles() {
        return motechRoleService.getRoles();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/roles/getrole", method = RequestMethod.POST)
    @ResponseBody public RoleDto getRole(@RequestBody String roleName) {
        return motechRoleService.getRole(roleName);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/roles/update", method = RequestMethod.POST)
    public void updateRole(@RequestBody RoleDto role) {
        motechRoleService.updateRole(role);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/roles/delete", method = RequestMethod.POST)
    public void deleteRole(@RequestBody RoleDto role) {
        motechRoleService.deleteRole(role);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/roles/create", method = RequestMethod.POST)
    public void saveRole(@RequestBody RoleDto role) {
        motechRoleService.createRole(role);
    }
}
