package org.motechproject.commcare.domain;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(JsonMethod.NONE)
public class SettingsDto {

    @JsonProperty
    private CommcareAccountSettings accountSettings;

    @JsonProperty
    private String eventStrategy;

    @JsonProperty
    private boolean forwardForms;
    @JsonProperty
    private boolean forwardCases;
    @JsonProperty
    private boolean forwardFormStubs;

    public SettingsDto() {
        this.accountSettings = new CommcareAccountSettings();
    }

    public String getCommcareBaseUrl() {
        return accountSettings.getCommcareBaseUrl();
    }

    public void setCommcareBaseUrl(String commcareBaseUrl) {
        this.accountSettings.setCommcareBaseUrl(commcareBaseUrl);
    }

    public String getCommcareDomain() {
        return accountSettings.getCommcareDomain();
    }

    public void setCommcareDomain(final String commcareDomain) {
        this.accountSettings.setCommcareDomain(commcareDomain);
    }

    public String getUsername() {
        return accountSettings.getUsername();
    }

    public void setUsername(final String username) {
        this.accountSettings.setUsername(username);
    }

    public String getPassword() {
        return accountSettings.getPassword();
    }

    public void setPassword(final String password) {
        this.accountSettings.setPassword(password);
    }

    public String getEventStrategy() {
        return eventStrategy;
    }

    public void setEventStrategy(final String eventStrategy) {
        this.eventStrategy = eventStrategy;
    }

    @JsonProperty("forwardForms")
    public boolean shouldForwardForms() {
        return forwardForms;
    }

    public void setForwardForms(boolean forwardForms) {
        this.forwardForms = forwardForms;
    }

    @JsonProperty("forwardCases")
    public boolean shouldForwardCases() {
        return forwardCases;
    }

    public void setForwardCases(boolean forwardCases) {
        this.forwardCases = forwardCases;
    }

    @JsonProperty("forwardFormStubs")
    public boolean shouldForwardFormStubs() {
        return forwardFormStubs;
    }

    public void setForwardFormStubs(boolean forwardFormStubs) {
        this.forwardFormStubs = forwardFormStubs;
    }
}
