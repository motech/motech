package org.motechproject.mrs.domain;


public interface User {

    String getUserId();

    void setUserId(String userId);

    String getSystemId();

    void setSystemId(String systemId);

    String getSecurityRole();

    void setSecurityRole(String securityRole);

    String getUserName();

    void setUserName(String userName);

    Person getPerson();

    void setPerson(Person person);
}
