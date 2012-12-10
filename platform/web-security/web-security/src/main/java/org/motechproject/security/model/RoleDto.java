package org.motechproject.security.model;

import org.motechproject.security.domain.MotechRole;

import java.util.List;

public class RoleDto {

    private String roleName;

    private String originalRoleName;

    private List<String> permissionNames;

    public RoleDto() {
        this(null, null);
    }

    public RoleDto(MotechRole motechRole) {
        this(motechRole.getRoleName(), motechRole.getPermissionNames());
    }

    public RoleDto(String roleName, List<String> permissionNames) {
        this.roleName = roleName;
        this.permissionNames = permissionNames;
        this.originalRoleName = roleName;
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
}

