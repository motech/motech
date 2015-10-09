package org.motechproject.security.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Unique;
import java.util.List;

@Entity(recordHistory = true)
public class MotechRole {

    @Field(required = true)
    @Unique
    private String roleName;

    @Field
    private List<String> permissionNames;

    @Field
    private boolean deletable;

    public MotechRole() {
        this(null, null, false);
    }

    public MotechRole(String roleName, List<String> permissionNames, boolean deletable) {
        this.roleName = roleName;
        this.permissionNames = permissionNames;
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

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public void removePermission(String permissionName) {
        if (permissionNames != null) {
            permissionNames.remove(permissionName);
        }
    }

    public boolean hasPermission(String permissionName) {
        return permissionNames != null && permissionNames.contains(permissionName);
    }

}
