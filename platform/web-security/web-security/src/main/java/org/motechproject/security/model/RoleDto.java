package org.motechproject.security.model;

import org.motechproject.security.domain.MotechRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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


    @Override
    public int hashCode() {
        return Objects.hash(roleName, originalRoleName, permissionNames);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final RoleDto other = (RoleDto) obj;

        return Objects.equals(this.roleName, other.roleName)
                && Objects.equals(this.originalRoleName, other.originalRoleName)
                && Objects.equals(this.permissionNames, other.permissionNames);
    }

    @Override
    public String toString() {
        return String.format(
                "RoleDto{roleName='%s', originalRoleName='%s', permissionNames=%s}",
                roleName, originalRoleName, permissionNames
        );
    }
}
