package org.motechproject.security.service;

import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.repository.AllMotechRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation for @AuthoritiesService.Given a MotechUser, retrieves the roles granted to that user
 * and for each role collects permissions associated with the role.
 */
@Service
public class AuthoritiesServiceImpl implements AuthoritiesService {

    @Autowired
    private AllMotechRoles allMotechRoles;

    @Override
    public List<GrantedAuthority> authoritiesFor(MotechUser user) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : user.getRoles()) {
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
