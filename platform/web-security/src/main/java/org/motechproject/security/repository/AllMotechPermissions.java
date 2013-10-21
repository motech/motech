package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechPermission;

import java.util.List;

/**
 * Interface for the permission repository, used for persisting permission objects.
 */
public interface AllMotechPermissions {

    void add(MotechPermission permission);

    MotechPermission findByPermissionName(String permissionName);

    List<MotechPermission> getPermissions();

    void delete(MotechPermission permission);
}
