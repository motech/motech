package org.motechproject.security.service;

import org.motechproject.security.constants.WebSecurityRoles;
import org.motechproject.security.model.PermissionDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for managing MOTECH permissions.
 */
public interface MotechPermissionService {

    /**
     * Gets list of all permissions
     *
     * @return list that contains permissions
     */
    @PreAuthorize("hasAnyRole('manageRoleAndPermission', 'manageURL', 'mdsSchemaAccess')")
    List<PermissionDto> getPermissions();

    /**
     * Finds permission by its name.
     *
     * @param name of the permission
     * @return permission of the given name
     */
    PermissionDto findPermissionByName(String name);

    /**
     * Adds a new permission
     *
     * @param permission to be added
     */
    void addPermission(PermissionDto permission);

    /**
     * Deletes permission with given name
     *
     * @param permissionName name of the permission to be removed
     */
    @PreAuthorize(WebSecurityRoles.HAS_MANAGE_ROLE_AND_PERMISSION)
    void deletePermission(String permissionName);
}
