package org.motechproject.openmrs.security;

import org.motechproject.mrs.security.MRSUser;
import org.openmrs.Role;
import org.openmrs.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenMRSUser extends MRSUser{
    private static Map<String, String> roleAuthorityMapping = new HashMap<String,String>(){{
        put("System Developer", "SuperAdmin");
        put("Create/Edit MoTeCH Data", "CallCenterAdmin");
    }};

    public OpenMRSUser(User user) {
        super(user.getName(), authoritiesFor(user));
    }

    private static List<GrantedAuthority> authoritiesFor(User user) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (final Role role : user.getRoles()) {
            authorities.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return roleAuthorityMapping.get(role.getRole());
                }
            });
        }
        return authorities;
    }

}
