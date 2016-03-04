package org.motechproject.security.web.controllers;

import org.motechproject.security.constants.WebSecurityRoles;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.service.MotechPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Controller used for CRUD operations on permission objects.
 */
@Controller
@RequestMapping("/web-api")
public class PermissionController {
    private MotechPermissionService motechPermissionService;

    /**
     * Gets list of permissions
     * @return list that contains all permissions
     */
    @RequestMapping(value = "/permissions", method = RequestMethod.GET)
    @ResponseBody
    public List<PermissionDto> getPermissions() {
        return motechPermissionService.getPermissions();
    }

    /**
     * Adds and saves new permission with given name
     * @param permissionName name of new permission
     */
    @PreAuthorize(WebSecurityRoles.HAS_MANAGE_ROLE_AND_PERMISSION)
    @RequestMapping(value = "/permissions/{permissionName}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void savePermission(@PathVariable String permissionName) {
        motechPermissionService.addPermission(new PermissionDto(permissionName, null));
    }

    /**
     * Deletes permission with given name
     * @param permissionName name of permission to be removed
     */
    @RequestMapping(value = "/permissions/{permissionName}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deletePermission(@PathVariable String permissionName) {
        motechPermissionService.deletePermission(permissionName);
    }

    @Autowired
    public void setMotechPermissionService(MotechPermissionService motechPermissionService) {
        this.motechPermissionService = motechPermissionService;
    }
}
