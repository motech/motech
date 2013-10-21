package org.motechproject.security.model;

import org.motechproject.security.domain.MotechPermission;

import java.io.Serializable;
import java.util.Objects;

/**
 * The <code>PermissionDto</code> contains information about permission.
 */
public class PermissionDto implements Serializable {

    private static final long serialVersionUID = 2763365338370830197L;

    private String permissionName;
    private String bundleName;

    public PermissionDto() {
    }

    public PermissionDto(MotechPermission motechPermission) {
        this(motechPermission.getPermissionName(), motechPermission.getBundleName());
    }

    public PermissionDto(String permissionName, String bundleName) {
        this.permissionName = permissionName;
        this.bundleName = bundleName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
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
