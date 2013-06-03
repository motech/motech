package org.motechproject.commcare.domain;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(JsonMethod.NONE)
public class CommcarePermission {

    @JsonProperty
    private String permissionName;

    @JsonProperty
    private Boolean granted;

    public CommcarePermission(String permissionName, Boolean granted) {
        this.permissionName = permissionName;
        this.granted = granted;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public Boolean getGranted() {
        return granted;
    }

    public void setGranted(Boolean granted) {
        this.granted = granted;
    }
}
