package org.motechproject.security.domain;

import org.motechproject.mds.annotations.Entity;

@Entity
public class MotechPermission {
    private String permissionName;
    private String bundleName;

    public MotechPermission() {
        this(null, null);
    }

    public MotechPermission(String permissionName, String bundleName) {
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
    public String toString() {
        return "MotechPermission{" +
                "permissionName='" + permissionName + '\'' +
                ", bundleName='" + bundleName + '\'' +
                '}';
    }
}
