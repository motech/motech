package org.motechproject.security.model;

import org.motechproject.security.domain.MotechRole;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer Motech role data between representations.
 *
 * Role data transfer object facilitates exchange of role data among services,
 * repository, and client user interface.
 */
public class RoleDto {

    private String roleName;

    private String originalRoleName;

    private List<String> permissionNames;

    private boolean deletable;

    public RoleDto() {
        this(null, new ArrayList<String>(), false);
    }

    public RoleDto(MotechRole motechRole) {
        this(motechRole.getRoleName(), motechRole.getPermissionNames(), motechRole.isDeletable());
    }

    public RoleDto(String roleName, List<String> permissionNames) {
        this(roleName, permissionNames, false);
    }

    public RoleDto(String roleName, List<String> permissionNames, boolean deletable) {
        this.roleName = roleName;
        this.permissionNames = permissionNames;
        this.originalRoleName = roleName;
        this.deletable = deletable;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<String> getPermissionNames() {
        return permissionNames;
    }

    public void setPermissionNames(List<String> permissionNames) {
        this.permissionNames = permissionNames;
    }

    public String getOriginalRoleName() {
        return originalRoleName;
    }

    public void setOriginalRoleName(String originalRoleName) {
        this.originalRoleName = originalRoleName;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }
}
