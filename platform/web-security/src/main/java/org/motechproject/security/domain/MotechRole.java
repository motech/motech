package org.motechproject.security.domain;

import java.util.List;

/**
 * Interface that represents Motech user roles.
 */
public interface MotechRole {

    String getRoleName();

    List<String> getPermissionNames();

    boolean isDeletable();

    void setRoleName(String roleName);

    void setPermissionNames(List<String> perrmissionNames);

    void setDeletable(boolean deletable);

    void removePermission(String permissionName);

    boolean hasPermission(String permissionName);
}
