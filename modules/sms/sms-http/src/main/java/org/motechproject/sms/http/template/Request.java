package org.motechproject.sms.http.template;

import org.motechproject.sms.http.domain.HttpMethodType;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String urlPath;
    private String recipientsSeparator;
    private Map<String, String> queryParameters = new HashMap<String, String>();
    private Map<String, String> bodyParameters = new HashMap<String, String>();
    private HttpMethodType type;
    private Authentication authentication;

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getRecipientsSeparator() {
        return recipientsSeparator;
    }

    public void setRecipientsSeparator(String recipientsSeparator) {
        this.recipientsSeparator = recipientsSeparator;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, String> queryParameters) {
        if (queryParameters != null) {
            this.queryParameters = queryParameters;
        }
    }

    public HttpMethodType getType() {
        return type;
    }

    public void setType(HttpMethodType type) {
        this.type = type;
    }

    public Map<String, String> getBodyParameters() {
        return bodyParameters;
    }

    public void setBodyParameters(Map<String, String> bodyParameters) {
        this.bodyParameters = bodyParameters;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public Authentication getAuthentication() {
        return authentication;
    }
}
