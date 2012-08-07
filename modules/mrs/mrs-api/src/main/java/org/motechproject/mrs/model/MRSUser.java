package org.motechproject.mrs.model;

import org.apache.commons.lang.ObjectUtils;

/**
 * Holds the information of MRS Staff
 */
public class MRSUser {
    private String id;
    private String systemId;
    private String securityRole;
    private String userName;
    private MRSPerson person;

    /**
     * Creates a MRS User
     * @param id User ID
     * @return
     */
    public MRSUser id(String id) {
        this.id = id;
        return this;
    }

    public MRSUser userName(String userName) {
        this.userName = userName;
        return this;
    }

    public MRSUser securityRole(String securityRole) {
        this.securityRole = securityRole;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public String getSecurityRole() {
        return securityRole;
    }

    public String getId() {
        return id;
    }

    public String getSystemId() {
        return systemId;
    }

    public MRSUser systemId(String systemId) {
        this.systemId = systemId;
        return this;
    }

    public MRSPerson getPerson() {
        return person;
    }

    public MRSUser person(MRSPerson person) {
        this.person = person;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MRSUser)) {
            return false;
        }
        MRSUser other = (MRSUser) o;
        if (!ObjectUtils.equals(id, other.id)) {
            return false;
        }
        if (!ObjectUtils.equals(systemId, other.systemId)) {
            return false;
        }
        if (!ObjectUtils.equals(securityRole, other.securityRole)) {
            return false;
        }
        if (!ObjectUtils.equals(userName, other.userName)) {
            return false;
        }
        if (!ObjectUtils.equals(person, other.person)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + ObjectUtils.hashCode(id);
        hash = hash * 31 + ObjectUtils.hashCode(systemId);
        hash = hash * 31 + ObjectUtils.hashCode(securityRole);
        hash = hash * 31 + ObjectUtils.hashCode(userName);
        hash = hash * 31 + ObjectUtils.hashCode(person);

        return hash;
    }
}
