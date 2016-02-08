package org.motechproject.metrics.security;

import static org.motechproject.metrics.security.Permissions.MANAGE_METRICS;
import static org.motechproject.metrics.security.Permissions.VIEW_METRICS;

public final class Roles {
    public static final String HAS_MANAGE_METRICS_ROLE = "hasRole('" + MANAGE_METRICS + "')";
    public static final String HAS_VIEW_METRICS_ROLE = "hasRole('" + VIEW_METRICS + "')";

    private Roles() {}
}
