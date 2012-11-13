package org.motechproject.security.domain;

import java.util.List;

public interface MotechUser {

    String getExternalId();

    String getUserName();

    String getPassword();

    String getEmail();

    List<String> getRoles();

    boolean isActive();

    void setActive(boolean active);

    void setPassword(String password);

    void setEmail(String email);

    void setUserName(String username);

    void setRoles(List<String> roles);

    void setExternalId(String externalId);
}
