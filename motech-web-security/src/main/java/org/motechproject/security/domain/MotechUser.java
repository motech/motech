package org.motechproject.security.domain;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface MotechUser {

    public String getExternalId();

    public String getUserName();

    public String getPassword();

    public List<String> getRoles();

    public List<GrantedAuthority> getAuthorities();

    public boolean isActive();

    public void setActive(boolean active);

    public void setPassword(String password);
}
