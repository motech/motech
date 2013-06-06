package org.motechproject.commons.api;

import org.apache.commons.lang.StringUtils;

import static java.lang.String.format;

public class Tenant {
    private TenantIdentity identity;

    Tenant(TenantIdentity identity) {
        this.identity = identity;
    }

    public String getId() {
        return identity.getId();
    }

    public boolean canHaveQueue(String queueName) {
        return StringUtils.isNotBlank(queueName) && queueName.startsWith(getId());
    }

    //should be replaced with a singleton once we have a better understanding of Tenant
    public static Tenant current() {
        return new Tenant(new TenantIdentity());
    }

    public String getSuffixedId() {
        return format("%s_", getId());
    }
}
