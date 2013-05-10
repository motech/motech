package org.motechproject.security.model;

import org.motechproject.security.domain.MotechPermission;

public class PermissionDto {

    private String permissionName;


    public PermissionDto() {
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
}
