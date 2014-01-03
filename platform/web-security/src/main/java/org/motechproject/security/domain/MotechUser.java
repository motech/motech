package org.motechproject.security.domain;

import java.util.List;
import java.util.Locale;

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

    String getOpenId();

    void setOpenId(String openId);

    Locale getLocale();

    void setLocale(Locale locale);

    boolean hasRole(String role);
}
