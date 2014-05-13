package org.motechproject.security.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

/**
 * Default implementation of {@link MotechPermission}.
 */
@TypeDiscriminator("doc.type == 'MotechPermission'")
public class MotechPermissionImpl extends MotechBaseDataObject implements MotechPermission {
    private static final long serialVersionUID = 7012900466673353433L;

    private String permissionName;
    private String bundleName;

    public MotechPermissionImpl() {
        this(null, null);
    }

    public MotechPermissionImpl(String permissionName, String bundleName) {
        super(MotechPermission.class.getSimpleName());
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
