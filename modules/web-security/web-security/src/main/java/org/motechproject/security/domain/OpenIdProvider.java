package org.motechproject.security.domain;

public interface OpenIdProvider {

    String getProviderName();

    String getProviderUrl();

    void setProviderName(String providerName);

    void setProviderUrl(String providerUrl);
}
