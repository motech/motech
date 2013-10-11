package org.motechproject.security.osgi.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.motechproject.security.constants.SecurityConfigConstants;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;

/**
 * A builder class for building security rules programatically
 * in order for manual testing. Typically rules should only be
 * created from JSON from modules or the UI.
 */
public final class SecurityTestConfigBuilder {

    public static final String USER_ACCESS_TEST = "addUserAccess";
    public static final String PERMISSION_ACCESS_TEST = "addPermissionAccess";
    public static final String METHOD_SPECIFIC_TEST = "addMethodSpecific";
    public static final String LOGIN_ACCESS_TEST = "addLoginAccess";
    public static final String NO_SECURITY_TEST = "noSecurity";

    private SecurityTestConfigBuilder() {
        //static class
    }

    public static MotechSecurityConfiguration buildConfig(String testOption, String configOption, String configOption2) {
        List<MotechURLSecurityRule> newRules = new ArrayList<MotechURLSecurityRule>();
        List<String> supportedSchemes = new ArrayList<>();
        Set<String> methodsRequired = new HashSet<>();
        List<String> permissionAccess = new ArrayList<>();
        List<String> userAccess = new ArrayList<>();

        MotechURLSecurityRule rule1 = new MotechURLSecurityRule();
        MotechURLSecurityRule rule2 = new MotechURLSecurityRule();

        rule1.setPattern("/**/web-api/**");
        rule1.setOrigin("test");
        rule1.setProtocol(SecurityConfigConstants.HTTP);
        rule1.setRest(true);
        rule1.setVersion("1");

        rule2.setPattern("/**");
        rule2.setOrigin("test");
        rule2.setProtocol(SecurityConfigConstants.HTTP);
        rule2.setRest(true);
        rule2.setVersion("1");

        newRules.add(rule1);
        newRules.add(rule2);

        switch (testOption) {
        case USER_ACCESS_TEST :
            userAccess.add(configOption);
            rule1.setUserAccess(userAccess);
            supportedSchemes.add(SecurityConfigConstants.BASIC);
            methodsRequired.add(SecurityConfigConstants.ANY_METHOD);
            break;
        case PERMISSION_ACCESS_TEST :
            permissionAccess.add(configOption);
            rule1.setPermissionAccess(permissionAccess);
            supportedSchemes.add(SecurityConfigConstants.BASIC);
            methodsRequired.add(SecurityConfigConstants.ANY_METHOD);
            break;
        case METHOD_SPECIFIC_TEST :
            supportedSchemes.add(SecurityConfigConstants.BASIC);
            methodsRequired.add(configOption);
            permissionAccess.add(configOption2);
            rule1.setPermissionAccess(permissionAccess);
            break;
        case LOGIN_ACCESS_TEST :
            supportedSchemes.add(SecurityConfigConstants.USERNAME_PASSWORD);
            supportedSchemes.add(SecurityConfigConstants.OPEN_ID);
            methodsRequired.add(SecurityConfigConstants.ANY_METHOD);
            rule1.setRest(false);
            break;
        case NO_SECURITY_TEST :
            newRules.remove(rule1);
            supportedSchemes.add(SecurityConfigConstants.NO_SECURITY);
            methodsRequired.add(SecurityConfigConstants.ANY_METHOD);
            break;
        default : break;
        }

        rule1.setMethodsRequired(methodsRequired);
        rule1.setSupportedSchemes(supportedSchemes);

        rule2.setMethodsRequired(methodsRequired);
        rule2.setSupportedSchemes(supportedSchemes);

        return new MotechSecurityConfiguration(newRules);
    }
}
