package org.motechproject.security.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RolesBundleIT.class, WebSecurityBundleIT.class, AllMotechPermissionsBundleIT.class,
        AllMotechRolesBundleIT.class, AllMotechWebUsersBundleIT.class, AllPasswordRecoveriesBundleIT.class,
        MotechRoleServiceBundleIT.class, MotechUserServiceBundleIT.class, SecurityRuleBuilderBundleIT.class,
        MotechProxyManagerBundleIT.class, AllMotechSecurityRulesBundleIT.class, MotechURLSecurityServiceBundleIT.class
})
public class WebSecurityBundleIntegrationTests {
}
