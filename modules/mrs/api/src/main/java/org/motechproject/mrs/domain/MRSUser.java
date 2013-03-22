package org.motechproject.mrs.domain;


public interface MRSUser {

    String getUserId();

    void setUserId(String userId);

    String getSystemId();

    void setSystemId(String systemId);

    String getSecurityRole();

    void setSecurityRole(String securityRole);

    String getUserName();

    void setUserName(String userName);

    MRSPerson getPerson();

    void setPerson(MRSPerson person);
}
