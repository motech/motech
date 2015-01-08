package org.motechproject.commons.api;

import org.apache.commons.lang.StringUtils;

import static java.lang.String.format;

/**
 * Class representing tenant used for getting queue statistics.
 */
public class Tenant {
    private TenantIdentity identity;

    /**
     * Constructor.
     *
     * @param identity  the identity of created tenant
     */
    Tenant(TenantIdentity identity) {
        this.identity = identity;
    }

    public String getId() {
        return identity.getId();
    }

    /**
     * Checks whether tenant can have given queue.
     *
     * @param queueName  the queue name to be checked
     * @return  true if tenant cant have given queue, false otherwise
     */
    public boolean canHaveQueue(String queueName) {
        return StringUtils.isNotBlank(queueName) && queueName.startsWith(getId());
    }

    //should be replaced with a singleton once we have a better understanding of Tenant
    /**
     * Returns current tenant.
     *
     * @return the current tenant
     */
    public static Tenant current() {
        return new Tenant(new TenantIdentity());
    }

    public String getSuffixedId() {
        return format("%s_", getId());
    }
}
