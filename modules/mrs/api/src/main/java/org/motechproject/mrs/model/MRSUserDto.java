package org.motechproject.mrs.model;

import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSUser;

public class MRSUserDto implements MRSUser {

    private String id;
    private String systemId;
    private String securityRole;
    private String userName;
    private MRSPerson person;

    public MRSUserDto() {
    }

    public MRSUserDto(String id, String systemId, String securityRole, String userName, MRSPerson person) {
        this.id = id;
        this.systemId = systemId;
        this.securityRole = securityRole;
        this.userName = userName;
        this.person = person;
    }

    public String getUserId() {
        return id;
    }

    public void setUserId(String id) {
        this.id = id;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSecurityRole() {
        return securityRole;
    }

    public void setSecurityRole(String securityRole) {
        this.securityRole = securityRole;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public MRSPerson getPerson() {
        return person;
    }

    public void setPerson(MRSPerson person) {
        this.person = person;
    }
}
