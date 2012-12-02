package org.motechproject.security.service;

import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.AllMotechUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MotechRoleServiceImpl implements MotechRoleService {

    @Autowired
    private AllMotechRoles allMotechRoles;

    @Autowired
    private AllMotechUsers allMotechUsers;

    @Override
    public List<RoleDto> getRoles() {
        List<RoleDto> roles = new ArrayList<>();
        for (MotechRole role : allMotechRoles.getRoles()) {
            roles.add(new RoleDto(role));
        }
        return roles;
    }

    @Override
    public RoleDto getRole(String roleName) {
        MotechRole motechRole = allMotechRoles.findByRoleName(roleName);
        return new RoleDto(motechRole);
    }

    @Override
    public void updateRole(RoleDto role) {
        MotechRole motechRole = allMotechRoles.findByRoleName(role.getOriginalRoleName());
        motechRole.setRoleName(role.getRoleName());
        motechRole.setPermissionNames(role.getPermissionNames());
        List<MotechUser> users = (List<MotechUser>) allMotechUsers.findByRole(role.getOriginalRoleName());
        for (MotechUser user : users) {
            List<String> roleList = user.getRoles();
            roleList.remove(role.getOriginalRoleName());
            roleList.add(role.getRoleName());
            allMotechUsers.update(user);
        }
        allMotechRoles.update(motechRole);
    }

    @Override
    public void deleteRole(RoleDto role) {
        MotechRole motechRole = allMotechRoles.findByRoleName(role.getRoleName());
        allMotechRoles.remove(motechRole);
    }

    @Override
    public void createRole(RoleDto role) {
        MotechRole motechRole = new MotechRoleCouchdbImpl(role.getRoleName(), role.getPermissionNames());
        allMotechRoles.add(motechRole);
    }
}
