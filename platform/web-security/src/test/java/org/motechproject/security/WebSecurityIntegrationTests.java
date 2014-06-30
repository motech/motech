package org.motechproject.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.motechproject.security.web.controllers.it.PermissionControllerSecurityIT;
import org.motechproject.security.web.controllers.it.RoleControllerSecurityIT;

@RunWith(Suite.class)
@Suite.SuiteClasses({PermissionControllerSecurityIT.class, RoleControllerSecurityIT.class})
public class WebSecurityIntegrationTests {
}
