package org.motechproject.security.model;

import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.UserStatus;

import java.util.List;
import java.util.Locale;

/**
 * Transfers Motech user data between representations
 */
public class UserDto {
    private String externalId;
    private String userName;
    private String password;
    private String email;
    private List<String> roles;
    private UserStatus userStatus;
    private String openId;
    private Locale locale;
    private boolean generatePassword;

    public UserDto() {
    }

    public UserDto(MotechUser motechUser) {
        this.externalId = motechUser.getExternalId();
        this.userName = motechUser.getUserName();
        this.email = motechUser.getEmail();
        this.roles = motechUser.getRoles();
        this.userStatus = motechUser.getUserStatus();
        this.openId = motechUser.getOpenId();
        this.locale = motechUser.getLocale();
        this.generatePassword = false;
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

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public boolean isGeneratePassword() {
        return generatePassword;
    }

    public void setGeneratePassword(boolean generatePassword) {
        this.generatePassword = generatePassword;
    }
}
