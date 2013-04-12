package org.motechproject.commons.api;

import org.apache.commons.lang.StringUtils;

public class TenantIdentity {
    private IdentityProvider identityProvider;

    public TenantIdentity() {
        this(new SystemIdentityProvider());
    }

    public TenantIdentity(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public String getId() {
        String identity = identityProvider.getIdentity();
        if (StringUtils.isNotBlank(identity)) {
            return identity.toLowerCase();
        }
        return identity;
    }
}
