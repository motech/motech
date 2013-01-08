package org.motechproject.testing.utils.server;

import java.util.Map;

public class RequestInfo {
    private final String contextPath;
    private final Map<String, String> queryParams;

    public RequestInfo(String contextPath, Map<String, String> queryParams) {
        this.contextPath = contextPath;
        this.queryParams = queryParams;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getQueryParam(String parameterName) {
        return queryParams.get(parameterName);
    }
}
