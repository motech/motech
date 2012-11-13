package org.motechproject.security.model;

import org.motechproject.security.domain.MotechUser;

import java.util.List;

public class UserDto {

    private String externalId;

    private String userName;

    private String password;

    private String email;

    private List<String> roles;

    private boolean active;

    public UserDto() {
    }

    public UserDto(MotechUser motechUser) {
        this.externalId = motechUser.getExternalId();
        this.userName = motechUser.getUserName();
        this.password = motechUser.getPassword();
        this.email = motechUser.getEmail();
        this.roles = motechUser.getRoles();
        this.active = motechUser.isActive();
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
