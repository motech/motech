package org.motechproject.security.model;

import org.motechproject.security.domain.MotechPermission;

public class PermissionDto {

    private String permissionName;

    private String bundleName;

    public PermissionDto() {
    }

    public PermissionDto(MotechPermission motechPermission) {
        this.permissionName = motechPermission.getPermissionName();
        this.bundleName = motechPermission.getBundleName();
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
