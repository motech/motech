package org.motechproject.security.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.List;

/**
 * Represent Motech user roles, with persistence in CouchDB.
 */
@TypeDiscriminator("doc.type == 'MotechRole'")
public class MotechRoleImpl extends MotechBaseDataObject implements MotechRole {
    private static final long serialVersionUID = 7042718621913820992L;

    private String roleName;
    private List<String> permissionNames;
    private boolean deletable;

    public MotechRoleImpl() {
        this(null, null, false);
    }

    public MotechRoleImpl(String roleName, List<String> permissionNames, boolean deletable) {
        super(MotechRole.class.getSimpleName());
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

    @Override
    public void removePermission(String permissionName) {
        if (permissionNames != null) {
            permissionNames.remove(permissionName);
        }
    }

    @Override
    public boolean hasPermission(String permissionName) {
        return permissionNames != null && permissionNames.contains(permissionName);
    }


}
