package org.motechproject.security.constants;

/**
 * A class for holding constants related to the options available for
 * dynamic security rules. MotechURLSecurityRule is where these
 * options are used. Prefixes related to security voting are also
 * stored in this class. Future security enhancements, such as OAUTH
 * should be added to this class and referenced from the
 * SecurityRuleBuilder.
 */
public final class SecurityConfigConstants {

    private SecurityConfigConstants() {

    }

    //Methods required config options
    public static final String ANY_METHOD = "ANY";
    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    public static final String HEAD_METHOD = "HEAD";
    public static final String OPTIONS_METHOD = "OPTIONS";
    public static final String DELETE_METHOD = "DELETE";
    public static final String TRACE_METHOD = "TRACE";

    //Supported schemes config options
    public static final String NO_SECURITY = "NO_SECURITY";
    public static final String USERNAME_PASSWORD = "USERNAME_PASSWORD";
    public static final String BASIC = "BASIC";
    public static final String OPEN_ID = "OPEN_ID";

    //Protocol config options
    public static final String HTTP = "HTTP";
    public static final String HTTPS = "HTTPS";

    //Pattern config options
    public static final String ANY_PATTERN = "ANY";

    //Motech user access voting prefix
    public static final String USER_ACCESS_PREFIX = "access_";

    //Motech role access voting prefix
    public static final String ROLE_ACCESS_PREFIX = "";
}


