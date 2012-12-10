package org.motechproject.security.model;

import org.motechproject.security.domain.MotechPermission;

public class PermissionDto {

    private String permissionName;

    private String bundleName;

    public PermissionDto() {
        this(null, null);
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
}
