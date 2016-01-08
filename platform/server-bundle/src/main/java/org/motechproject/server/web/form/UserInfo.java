package org.motechproject.server.web.form;

import java.util.Objects;

public class UserInfo {
    private final String userName;
    private final boolean securityLaunch;
    private final String lang;

    public UserInfo(String userName, boolean securityLaunch, String lang) {
        this.userName = userName;
        this.securityLaunch = securityLaunch;
        this.lang = lang;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isSecurityLaunch() {
        return securityLaunch;
    }

    public String getLang() {
        return lang;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, securityLaunch, lang);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final UserInfo other = (UserInfo) obj;

        return Objects.equals(this.userName, other.userName)
                && Objects.equals(this.securityLaunch, other.securityLaunch)
                && Objects.equals(this.lang, other.lang);
    }

    @Override
    public String toString() {
        return String.format(
                "UserInfo{userName='%s', securityLaunch=%s, lang='%s'}",
                userName, securityLaunch, lang
        );
    }
}
