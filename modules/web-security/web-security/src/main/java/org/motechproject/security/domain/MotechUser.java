package org.motechproject.security.domain;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface MotechUser {

    String getExternalId();

    String getUserName();

    String getPassword();

    List<String> getRoles();

    List<GrantedAuthority> getAuthorities();

    boolean isActive();

    void setActive(boolean active);

    void setPassword(String password);
}
