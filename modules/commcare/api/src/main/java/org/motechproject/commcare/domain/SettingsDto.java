package org.motechproject.commcare.domain;

public class SettingsDto {
    private String commcareDomain;
    private String username;
    private String password;
    private String eventStrategy;

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

}
