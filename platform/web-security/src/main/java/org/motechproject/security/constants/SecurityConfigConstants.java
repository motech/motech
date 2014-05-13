package org.motechproject.security.constants;

/**
 * A class for holding constants related to the options available for dynamic security rules.
 * MotechURLSecurityRule is where these options are used. Prefixes related to security voting are
 * also stored in this class.
 */
public final class SecurityConfigConstants {

    private SecurityConfigConstants() {
    }

    //Pattern config options
    public static final String ANY_PATTERN = "ANY";

    //Motech user access voting prefix
    public static final String USER_ACCESS_PREFIX = "access_";

    //Motech role access voting prefix
    public static final String ROLE_ACCESS_PREFIX = "";
}


