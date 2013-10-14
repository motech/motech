package org.motechproject.security.service;

import org.motechproject.security.model.PermissionDto;

import java.util.List;


public interface MotechPermissionService {

    List<PermissionDto> getPermissions();

    void addPermission(PermissionDto permission);
}
