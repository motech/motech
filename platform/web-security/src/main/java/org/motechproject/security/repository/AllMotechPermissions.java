package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechPermission;

import java.util.List;

public interface AllMotechPermissions {

    void add(MotechPermission permission);

    MotechPermission findByPermissionName(String permissionName);

    List<MotechPermission> getPermissions();
}
