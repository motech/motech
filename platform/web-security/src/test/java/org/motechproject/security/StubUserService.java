package org.motechproject.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class StubUserService implements UserDetailsService {

    public static final String USER_WITH_PERMISSION_TO_MANAGE_ROLES = "userWithPermissionToManageRoles";
    public static final String USER_WITHOUT_PERMISSION_TO_MANAGE_ROLES = "userWithoutPermissionToManageRoles";
    private static final String MANAGE_ROLES_PERMISSION = "manageRole";
    private static final String USELESS_ROLE = "uselessRole";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        SimpleGrantedAuthority authority = USER_WITH_PERMISSION_TO_MANAGE_ROLES.equals(username) ? new SimpleGrantedAuthority(MANAGE_ROLES_PERMISSION) : new SimpleGrantedAuthority(USELESS_ROLE);
        grantedAuthorities.add(authority);
        return new User("testUser", "testPassword", true, true, true, true, grantedAuthorities);
    }
}
