package org.motechproject.security.service.impl;

import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.ex.RoleHasUserException;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to manage roles in Motech
 *
 * @see MotechRole
 */
@Service("motechRoleService")
public class MotechRoleServiceImpl implements MotechRoleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MotechRoleServiceImpl.class);

    private AllMotechRoles allMotechRoles;
    private AllMotechUsers allMotechUsers;
    private UserContextService userContextsService;

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
        return motechRole != null ? new RoleDto(motechRole) : null;
    }

    @Override
    public void updateRole(RoleDto role) {
        LOGGER.info("Updating role: {}", role.getRoleName());
        MotechRole motechRole = allMotechRoles.findByRoleName(role.getOriginalRoleName());

        motechRole.setRoleName(role.getRoleName());
        motechRole.setPermissionNames(role.getPermissionNames());
        List<MotechUser> users = allMotechUsers.findByRole(role.getOriginalRoleName());

        for (MotechUser user : users) {
            List<String> roleList = user.getRoles();
            roleList.remove(role.getOriginalRoleName());
            roleList.add(role.getRoleName());
            allMotechUsers.update(user);
        }

        allMotechRoles.update(motechRole);
        userContextsService.refreshAllUsersContextIfActive();
        LOGGER.info("Updated role: {}", role.getRoleName());
    }

    @Override
    public void deleteRole(RoleDto role) {
        LOGGER.info("Deleting role: {}", role.getRoleName());
        MotechRole motechRole = allMotechRoles.findByRoleName(role.getRoleName());
        if (motechRole.isDeletable()) {
            List<MotechUser> users = allMotechUsers.findByRole(role.getRoleName());
            if (!users.isEmpty()) {
                throw new RoleHasUserException("Role cannot be deleted because a user has the role.");
            }
            allMotechRoles.remove(motechRole);
            userContextsService.refreshAllUsersContextIfActive();
            LOGGER.info("Deleted role: {}", role);
        } else {
            LOGGER.warn("The role {} cant be deleted", role.getRoleName());
        }

    }

    @Override
    public void createRole(RoleDto role) {
        LOGGER.info("Creating role: {}", role.getRoleName());
        MotechRole motechRole = new MotechRole(role.getRoleName(), role.getPermissionNames(),
                role.isDeletable());
        allMotechRoles.add(motechRole);
        userContextsService.refreshAllUsersContextIfActive();
        LOGGER.info("Created role: {}", role.getRoleName());
    }

    @Autowired
    public void setAllMotechRoles(AllMotechRoles allMotechRoles) {
        this.allMotechRoles = allMotechRoles;
    }

    @Autowired
    public void setAllMotechUsers(AllMotechUsers allMotechUsers) {
        this.allMotechUsers = allMotechUsers;
    }

    @Autowired
    public void setUserContextsService(UserContextService userContextsService) {
        this.userContextsService = userContextsService;
    }
}
