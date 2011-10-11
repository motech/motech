package org.motechproject.openmrs.security;

import org.motechproject.mrs.security.MRSUser;
import org.openmrs.Role;
import org.openmrs.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class OpenMRSUser extends MRSUser{

    private String password;

    public OpenMRSUser(User user, String password) {
        super(user.getSystemId(), authoritiesFor(user));
        this.password = password;
    }

    private static List<GrantedAuthority> authoritiesFor(User user) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (final Role role : user.getRoles()) {
            authorities.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return role.getRole();
                }
            });
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
