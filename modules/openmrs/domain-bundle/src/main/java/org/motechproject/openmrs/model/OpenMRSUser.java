package org.motechproject.openmrs.model;

import org.apache.commons.lang.ObjectUtils;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSUser;

/**
 * Holds the information of MRS Staff
 */
public class OpenMRSUser implements MRSUser {

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
    @Deprecated
    public OpenMRSUser id(String id) {
        this.id = id;
        return this;
    }

    @Deprecated
    public OpenMRSUser userName(String userName) {
        this.userName = userName;
        return this;
    }

    @Deprecated
    public OpenMRSUser securityRole(String securityRole) {
        this.securityRole = securityRole;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public String getSecurityRole() {
        return securityRole;
    }

    public String getSystemId() {
        return systemId;
    }

    @Deprecated
    public OpenMRSUser systemId(String systemId) {
        this.systemId = systemId;
        return this;
    }

    public MRSPerson getPerson() {
        return person;
    }

    @Deprecated
    public OpenMRSUser person(MRSPerson person) {
        this.person = person;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OpenMRSUser)) {
            return false;
        }
        OpenMRSUser other = (OpenMRSUser) o;
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

    @Override
    public String getUserId() {
        return id;
    }

    @Override
    public void setUserId(String id) {
        this.id = id;
    }

    @Override
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public void setSecurityRole(String securityRole) {
        this.securityRole = securityRole;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void setPerson(MRSPerson person) {
        this.person = person;
    }
}
