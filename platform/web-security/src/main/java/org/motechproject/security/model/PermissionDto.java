package org.motechproject.security.model;

import org.motechproject.security.domain.MotechPermission;

import java.util.Objects;

/**
 * The <code>PermissionDto</code> contains information about permission.
 */
public class PermissionDto {
    private String permissionName;

    public PermissionDto() {
        this((String) null);
    }

    public PermissionDto(MotechPermission motechPermission) {
        this(motechPermission.getPermissionName());
    }

    public PermissionDto(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final PermissionDto other = (PermissionDto) obj;

        return Objects.equals(this.permissionName, other.permissionName);
    }

    @Override
    public String toString() {
        return String.format(
                "PermissionDto{permissionName='%s'}",
                permissionName
        );
    }
}
