package org.motechproject.commcare.domain;

import org.apache.commons.lang.StringUtils;

public class SettingsDto {
    private String commcareBaseUrl;
    private String commcareDomain;
    private String username;
    private String password;
    private String eventStrategy;

    public String getCommcareBaseUrl() {
        return commcareBaseUrl;
    }

    public void setCommcareBaseUrl(String commcareBaseUrl) {
        this.commcareBaseUrl = commcareBaseUrl;
    }

    public String getCommcareDomain() {
        return commcareDomain;
    }

    public void setCommcareDomain(final String commcareDomain) {
        this.commcareDomain = commcareDomain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getEventStrategy() {
        return eventStrategy;
    }

    public void setEventStrategy(final String eventStrategy) {
        this.eventStrategy = eventStrategy;
    }

    public boolean isValid() {
        if (StringUtils.isBlank(commcareBaseUrl)) {
            return false;
        }

        if (StringUtils.isBlank(commcareDomain)) {
            return false;
        }

        if (StringUtils.isBlank(username)) {
            return false;
        }

        if (StringUtils.isBlank(password)) {
            return false;
        }

        if (StringUtils.isBlank(eventStrategy)) {
            return false;
        }

        return true;
    }

}
