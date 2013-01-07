package org.motechproject.security.helper;

import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.repository.AllMotechRoles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public final class SecurityHelper {

    private SecurityHelper() {
        // static utility class
    }

    public static List<GrantedAuthority> getAuthorities(List<String> roles, AllMotechRoles allMotechRoles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            MotechRole motechRole = allMotechRoles.findByRoleName(role);
            if (motechRole != null) {
                for (String permission : motechRole.getPermissionNames()) {
                    authorities.add(new SimpleGrantedAuthority(permission));
                }
            }
        }
        return authorities;
    }
}
