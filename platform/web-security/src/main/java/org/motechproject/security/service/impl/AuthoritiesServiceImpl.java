package org.motechproject.security.service.impl;

import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.service.AuthoritiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthoritiesServiceImpl.class);

    private AllMotechRoles allMotechRoles;

    @Autowired
    public AuthoritiesServiceImpl(AllMotechRoles allMotechRoles) {
        this.allMotechRoles = allMotechRoles;
    }

    @Override
    public List<GrantedAuthority> authoritiesFor(MotechUser user) {
        LOGGER.debug("Looking up authorities for MotechUser: " + user);

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : user.getRoles()) {
            LOGGER.trace("Looking up role: " + role);
            MotechRole motechRole = allMotechRoles.findByRoleName(role);
            LOGGER.trace("Checking permissions in role: " + motechRole);
            if (motechRole != null) {
                for (String permission : motechRole.getPermissionNames()) {
                    LOGGER.trace("Adding new permission to the list of granted authorities: " + permission);
                    authorities.add(new SimpleGrantedAuthority(permission));
                }
            }
        }
        return authorities;
    }
}
