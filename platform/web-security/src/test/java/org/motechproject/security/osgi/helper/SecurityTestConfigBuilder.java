package org.motechproject.security.osgi.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.motechproject.security.constants.HTTPMethod;
import org.motechproject.security.constants.Protocol;
import org.motechproject.security.constants.Scheme;
import org.motechproject.security.constants.SecurityConfigConstants;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;

import static org.motechproject.security.constants.Protocol.*;

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

    public static MotechSecurityConfiguration buildConfig(String testOption, Object configOption, String configOption2) {
        List<MotechURLSecurityRule> newRules = new ArrayList<MotechURLSecurityRule>();
        List<Scheme> supportedSchemes = new ArrayList<>();
        Set<HTTPMethod> methodsRequired = new HashSet<>();
        List<String> permissionAccess = new ArrayList<>();
        List<String> userAccess = new ArrayList<>();

        MotechURLSecurityRule rule1 = new MotechURLSecurityRule();
        MotechURLSecurityRule rule2 = new MotechURLSecurityRule();

        rule1.setPattern("/**/web-api/**");
        rule1.setOrigin("test");
        rule1.setProtocol(HTTP);
        rule1.setRest(true);
        rule1.setVersion("1");

        rule2.setPattern("/**");
        rule2.setOrigin("test");
        rule2.setProtocol(HTTP);
        rule2.setRest(true);
        rule2.setVersion("1");

        newRules.add(rule1);
        newRules.add(rule2);

        switch (testOption) {
        case USER_ACCESS_TEST :
            userAccess.add((String) configOption);
            rule1.setUserAccess(userAccess);
            supportedSchemes.add(Scheme.BASIC);
            methodsRequired.add(HTTPMethod.ANY);
            break;
        case PERMISSION_ACCESS_TEST :
            permissionAccess.add((String) configOption);
            rule1.setPermissionAccess(permissionAccess);
            supportedSchemes.add(Scheme.BASIC);
            methodsRequired.add(HTTPMethod.ANY);
            break;
        case METHOD_SPECIFIC_TEST :
            supportedSchemes.add(Scheme.BASIC);
            methodsRequired.add((HTTPMethod) configOption);
            permissionAccess.add(configOption2);
            rule1.setPermissionAccess(permissionAccess);
            break;
        case LOGIN_ACCESS_TEST :
            supportedSchemes.add(Scheme.USERNAME_PASSWORD);
            supportedSchemes.add(Scheme.OPEN_ID);
            methodsRequired.add(HTTPMethod.ANY);
            rule1.setRest(false);
            break;
        case NO_SECURITY_TEST :
            newRules.remove(rule1);
            supportedSchemes.add(Scheme.NO_SECURITY);
            methodsRequired.add(HTTPMethod.ANY);
            break;
        default : break;
        }

        rule1.setMethodsRequired(methodsRequired);
        rule1.setSupportedSchemes(supportedSchemes);
        rule1.setActive(true);

        rule2.setMethodsRequired(methodsRequired);
        rule2.setSupportedSchemes(supportedSchemes);
        rule2.setActive(true);

        return new MotechSecurityConfiguration(newRules);
    }
}
