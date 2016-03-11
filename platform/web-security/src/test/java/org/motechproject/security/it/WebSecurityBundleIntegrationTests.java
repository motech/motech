package org.motechproject.security.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RolesBundleIT.class, WebSecurityBundleIT.class, MotechPermissionServiceBundleIT.class,
        PasswordRecoveryServiceBundleIT.class, MotechRoleServiceBundleIT.class, MotechUserServiceBundleIT.class,
        MotechProxyManagerBundleIT.class, MotechURLSecurityServiceBundleIT.class
})
public class WebSecurityBundleIntegrationTests {
}
