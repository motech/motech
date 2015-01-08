package org.motechproject.commons.api;

import org.apache.commons.lang.StringUtils;

/**
 * Holds identity of a tenant.
 */
public class TenantIdentity {

    private IdentityProvider identityProvider;

    /**
     * Constructor.
     */
    public TenantIdentity() {
        this(new SystemIdentityProvider());
    }

    /**
     * Constructor.
     *
     * @param identityProvider  the identity provider to be used
     */
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
