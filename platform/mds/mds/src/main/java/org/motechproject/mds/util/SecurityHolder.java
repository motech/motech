package org.motechproject.mds.util;

import java.util.Set;

public class SecurityHolder {

    private SecurityMode securityMode;
    private SecurityMode readOnlySecurityMode;
    private Set<String> securityMembers;
    private Set<String> readOnlySecurityMembers;

    public SecurityHolder() {
    }

    public SecurityHolder(SecurityMode securityMode, SecurityMode readOnlySecurityMode, Set<String> securityMembers,
                          Set<String> readOnlySecurityMembers) {
        this.securityMode = securityMode;
        this.readOnlySecurityMode = readOnlySecurityMode;
        this.securityMembers = securityMembers;
        this.readOnlySecurityMembers = readOnlySecurityMembers;
    }

    public SecurityMode getSecurityMode() {
        return securityMode;
    }

    public void setSecurityMode(SecurityMode securityMode) {
        this.securityMode = securityMode;
    }

    public SecurityMode getReadOnlySecurityMode() {
        return readOnlySecurityMode;
    }

    public void setReadOnlySecurityMode(SecurityMode readOnlySecurityMode) {
        this.readOnlySecurityMode = readOnlySecurityMode;
    }

    public Set<String> getSecurityMembers() {
        return securityMembers;
    }

    public void setSecurityMembers(Set<String> securityMembers) {
        this.securityMembers = securityMembers;
    }

    public Set<String> getReadOnlySecurityMembers() {
        return readOnlySecurityMembers;
    }

    public void setReadOnlySecurityMembers(Set<String> readOnlySecurityMembers) {
        this.readOnlySecurityMembers = readOnlySecurityMembers;
    }
}
