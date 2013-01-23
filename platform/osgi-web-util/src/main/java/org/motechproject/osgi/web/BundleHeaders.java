package org.motechproject.osgi.web;

import org.osgi.framework.Bundle;

import java.util.Dictionary;

public class BundleHeaders {

    private final Dictionary headers;

    public BundleHeaders(Bundle bundle) {
        headers = bundle.getHeaders();
    }

    public Object get(Object key) {
        return headers.get(key);
    }

    public String getContextPath() {
        return getStringValue("Context-Path");
    }

    public String getResourcePath() {
        return getStringValue("Resource-Path");
    }

    public String getStringValue(String key) {
        return (String) get(key);
    }

    public boolean isBluePrintEnabled() {
        return Boolean.valueOf(getStringValue("Blueprint-Enabled"));
    }
}
