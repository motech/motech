package org.motechproject.security.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({PermissionControllerSecurityContextIT.class, RoleControllerSecurityContextIT.class})
public class WebSecurityIntegrationTests {
}
