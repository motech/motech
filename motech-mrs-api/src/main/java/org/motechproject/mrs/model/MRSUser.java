package org.motechproject.mrs.model;

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

}
