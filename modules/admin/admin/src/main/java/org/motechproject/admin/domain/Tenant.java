package org.motechproject.admin.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.TenantIdentity;

public class Tenant {
    private String tenantId;

    Tenant(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getId() {
        return tenantId;
    }

    public boolean canHaveQueue(String queueName) {
        return StringUtils.isNotBlank(queueName) && queueName.startsWith(tenantId);
    }

    public static Tenant current() {
        return new Tenant(TenantIdentity.getTenantId());
    }
}
