package org.motechproject.security.service.impl;

import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.AuthoritiesService;
import org.motechproject.security.service.MotechRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation for {@link AuthoritiesService}. Given a MotechUser, retrieves the roles granted to that user
 * and for each role collects permissions associated with the role.
 */
@Service
public class AuthoritiesServiceImpl implements AuthoritiesService {

    private MotechRoleService motechRoleService;

    @Override
    @Transactional
    public List<GrantedAuthority> authoritiesFor(MotechUser user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : user.getRoles()) {
            RoleDto motechRole = motechRoleService.getRole(role);
            if (motechRole != null) {
                for (String permission : motechRole.getPermissionNames()) {
                    authorities.add(new SimpleGrantedAuthority(permission));
                }
            }
        }
        return authorities;
    }

    @Autowired
    public void setMotechRoleService(MotechRoleService motechRoleService) {
        this.motechRoleService = motechRoleService;
    }

}
