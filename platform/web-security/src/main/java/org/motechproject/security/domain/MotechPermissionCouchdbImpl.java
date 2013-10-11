package org.motechproject.security.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'MotechPermission'")
public class MotechPermissionCouchdbImpl extends MotechBaseDataObject implements MotechPermission {

    public static final String DOC_TYPE = "MotechPermission";

    @JsonProperty
    private String permissionName;

    @JsonProperty
    private String bundleName;

    public MotechPermissionCouchdbImpl() {
        super();
        this.setType(DOC_TYPE);
    }

    public MotechPermissionCouchdbImpl(String permissionName, String bundleName) {
        super();
        this.permissionName = permissionName;
        this.bundleName = bundleName;
        this.setType(DOC_TYPE);
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
