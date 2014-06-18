package org.motechproject.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.motechproject.security.osgi.AllMotechPermissionIT;
import org.motechproject.security.osgi.AllMotechRoleIT;
import org.motechproject.security.osgi.AllMotechSecurityRulesIT;
import org.motechproject.security.osgi.AllMotechWebUsersIT;
import org.motechproject.security.osgi.AllPasswordRecoveriesIT;
import org.motechproject.security.osgi.MotechProxyManagerIT;
import org.motechproject.security.osgi.MotechRoleServiceIT;
import org.motechproject.security.osgi.MotechURLSecurityServiceIT;
import org.motechproject.security.osgi.MotechUserServiceIT;
import org.motechproject.security.osgi.RolesBundleIT;
import org.motechproject.security.osgi.SecurityRuleBuilderIT;
import org.motechproject.security.osgi.WebSecurityBundleIT;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RolesBundleIT.class, WebSecurityBundleIT.class, AllMotechPermissionIT.class,
        AllMotechRoleIT.class, AllMotechWebUsersIT.class, AllPasswordRecoveriesIT.class,
        MotechRoleServiceIT.class, MotechUserServiceIT.class, SecurityRuleBuilderIT.class,
        MotechProxyManagerIT.class, AllMotechSecurityRulesIT.class, MotechURLSecurityServiceIT.class
})
public class WebSecurityBundleIntegrationTests {
}
