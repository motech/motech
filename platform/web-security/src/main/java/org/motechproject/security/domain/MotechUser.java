package org.motechproject.security.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.security.constants.PermissionNames;

import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Entity that represents Motech user
 */
@Entity(recordHistory = true)
@Access(value = SecurityMode.PERMISSIONS, members = {PermissionNames.MANAGE_USER_PERMISSION})
public class MotechUser {

    @Field
    private String externalId;

    @Field
    private String userName;

    @Field
    private String password;

    @Field
    private String email;

    @Field
    private List<String> roles;

    @Field(defaultValue = "ACTIVE")
    private UserStatus userStatus;

    @Field
    private String openId;

    @Field
    private Locale locale;

    @Field
    private DateTime lastPasswordChange;

    @Field(defaultValue = "0")
    private Integer failureLoginCounter;

    public MotechUser() {
        this(null, null, null, null, null, null, null);
    }

    public MotechUser(String userName, String password, String email, String externalId,
                      List<String> roles, String openId, Locale locale) {
        this.userName = userName == null ? null : userName.toLowerCase();
        this.password = password;
        this.email = isBlank(email) ? null : email;
        this.externalId = externalId;
        this.roles = roles;
        this.userStatus = UserStatus.ACTIVE;
        this.openId = openId;
        this.locale = locale;
        this.lastPasswordChange = DateUtil.now();
        this.failureLoginCounter = 0;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (!StringUtils.equals(this.password, password)) {
            lastPasswordChange = DateUtil.now();
        }
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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

    public Integer getFailureLoginCounter() {
        if (failureLoginCounter == null) {
            return 0;
        }
        return failureLoginCounter;
    }

    public void setFailureLoginCounter(Integer failureLoginCounter) {
        this.failureLoginCounter = failureLoginCounter;
    }

    public boolean hasRole(String role) {
        return CollectionUtils.isNotEmpty(roles) && roles.contains(role);
    }

    public DateTime getLastPasswordChange() {
        return lastPasswordChange;
    }

    public void setLastPasswordChange(DateTime lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }

    @Ignore
    public boolean isActive() {
        if (UserStatus.BLOCKED.equals(this.userStatus)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MotechUser that = (MotechUser) o;

        return userName.equals(that.userName);
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", userName, email);
    }
}
