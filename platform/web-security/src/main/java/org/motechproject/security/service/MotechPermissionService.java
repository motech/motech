package org.motechproject.security.service;

import org.motechproject.security.model.PermissionDto;

import java.util.List;

/**
 * Service for managing Motech permissions.
 */
public interface MotechPermissionService {

    /**
     * Gets list of all permissions
     *
     * @return list that contains permissions
     */
    List<PermissionDto> getPermissions();

    /**
     * Adds new permission
     * @param permission to be added
     */
    void addPermission(PermissionDto permission);

    /**
     * Deletes permission with given name
     *
     * @param permissionName name of the permission to be removed
     */
    void deletePermission(String permissionName);
}
