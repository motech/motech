package org.motechproject.openmrs.security;

import org.junit.Test;
import org.openmrs.Role;
import org.openmrs.User;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.util.HashSet;

public class OpenMRSUserTest {
    @Test
    public void shouldMapSuperAdminRole() {
        User user = new User();
        user.setRoles(new HashSet<Role>(){{
            add(new Role("System Developer"));
        }});
        new OpenMRSUser(user).getAuthorities().contains(new GrantedAuthorityImpl("SuperAdmin"));
    }

    @Test
    public void shouldMapCallCenterAdminRole() {
        User user = new User();
        user.setRoles(new HashSet<Role>(){{
            add(new Role("Create/Edit MoTeCH Data"));
        }});
        new OpenMRSUser(user).getAuthorities().contains(new GrantedAuthorityImpl("CallCenterAdmin"));
    }
}
