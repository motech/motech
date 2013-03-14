package org.motechproject.mrs.model;

import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.User;

public class UserDto implements User {

    private String id;
    private String systemId;
    private String securityRole;
    private String userName;
    private Person person;

    public UserDto() {
    }

    public UserDto(String id, String systemId, String securityRole, String userName, Person person) {
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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
