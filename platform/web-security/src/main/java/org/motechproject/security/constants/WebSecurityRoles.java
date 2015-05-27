package org.motechproject.security.constants;

/**
 * Contains constants used for securing parts of the web-security module.
 */
public final class WebSecurityRoles {
    private static final String HAS_ROLE = "hasRole";

    public static final String HAS_MANAGE_ROLE_AND_PERMISSION =
            HAS_ROLE + "('" + PermissionNames.MANAGE_ROLE_AND_PERMISSION_PERMISSION + "')";

    public static final String HAS_MANAGE_USER = HAS_ROLE + "('" + PermissionNames.MANAGE_USER_PERMISSION + "')";
    public static final String HAS_MANAGE_URL = HAS_ROLE + "('" + PermissionNames.MANAGE_URL_PERMISSION + "')";

    private WebSecurityRoles() {

    }
}
