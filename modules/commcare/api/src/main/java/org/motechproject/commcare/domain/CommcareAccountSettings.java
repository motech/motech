package org.motechproject.commcare.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(JsonMethod.NONE)
public class CommcareAccountSettings {

    @JsonProperty
    private String commcareBaseUrl;
    @JsonProperty
    private String commcareDomain;
    @JsonProperty
    private String username;
    @JsonProperty
    private String password;

    public String getCommcareBaseUrl() {
        return commcareBaseUrl != null? commcareBaseUrl : StringUtils.EMPTY;
    }

    public void setCommcareBaseUrl(String commcareBaseUrl) {
        this.commcareBaseUrl = commcareBaseUrl;
    }

    public String getCommcareDomain() {
        return commcareDomain != null? commcareDomain : StringUtils.EMPTY;
    }

    public void setCommcareDomain(final String commcareDomain) {
        this.commcareDomain = commcareDomain;
    }

    public String getUsername() {
        return username != null? username : StringUtils.EMPTY;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password != null? password : StringUtils.EMPTY;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public boolean canMakeConnection() {
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
        return true;
    }
}
