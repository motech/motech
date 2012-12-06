package org.motechproject.security.repository;

import org.motechproject.security.domain.OpenIdProvider;

import java.util.List;

public interface AllOpenIdProviders {

    OpenIdProvider findByName(String name);

    OpenIdProvider findByUrl(String url);

    List<OpenIdProvider> getProviders();

    void add(OpenIdProvider provider);

    void update(OpenIdProvider provider);

    void remove(OpenIdProvider provider);
}
