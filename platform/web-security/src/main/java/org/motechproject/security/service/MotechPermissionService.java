package org.motechproject.security.service;

import org.motechproject.security.model.PermissionDto;

import java.util.List;

/**
 * Service for managing Motech permissions.
 */
public interface MotechPermissionService {

    List<PermissionDto> getPermissions();

    void addPermission(PermissionDto permission);

    void deletePermission(String permissionName);
}
