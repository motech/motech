package org.motechproject.security.service.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.exception.RoleHasUserException;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.repository.MotechUsersDao;
import org.motechproject.security.service.mds.MotechRolesDataService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link org.motechproject.security.service.MotechRoleService}
 * Service to manage roles in Motech
 *
 * @see MotechRole
 */
@Service("motechRoleService")
public class MotechRoleServiceImpl implements MotechRoleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MotechRoleServiceImpl.class);

    private MotechRolesDataService motechRolesDataService;
    private MotechUsersDao motechUsersDao;
    private UserContextService userContextsService;

    @Override
    @Transactional
    public List<RoleDto> getRoles() {
        List<RoleDto> roles = new ArrayList<>();
        for (MotechRole role : motechRolesDataService.retrieveAll()) {
            roles.add(new RoleDto(role));
        }
        return roles;
    }

    @Override
    @Transactional
    public RoleDto getRole(String roleName) {
        MotechRole motechRole = findByRoleName(roleName);
        return motechRole != null ? new RoleDto(motechRole) : null;
    }

    @Override
    @Transactional
    public void updateRole(RoleDto role) {
        LOGGER.info("Updating role: {}", role.getRoleName());
        MotechRole motechRole = findByRoleName(role.getOriginalRoleName());

        motechRole.setRoleName(role.getRoleName());
        motechRole.setPermissionNames(role.getPermissionNames());
        List<MotechUser> users = motechUsersDao.findByRole(role.getOriginalRoleName());

        if (StringUtils.equals(role.getRoleName(), role.getOriginalRoleName())) {
            for (MotechUser user : users) {
                List<String> roleList = user.getRoles();
                roleList.remove(role.getOriginalRoleName());
                roleList.add(role.getRoleName());
                motechUsersDao.update(user);
            }
        }

        motechRolesDataService.update(motechRole);
        userContextsService.refreshAllUsersContextIfActive();

        LOGGER.info("Updated role: {}", role.getRoleName());
    }

    @Override
    @Transactional
    public void deleteRole(RoleDto role) {
        LOGGER.info("Deleting role: {}", role.getRoleName());
        MotechRole motechRole = findByRoleName(role.getRoleName());
        if (motechRole.isDeletable()) {
            List<MotechUser> users = motechUsersDao.findByRole(role.getRoleName());
            if (!users.isEmpty()) {
                throw new RoleHasUserException("Role cannot be deleted because a user has the role.");
            }
            motechRolesDataService.delete(motechRole);
            userContextsService.refreshAllUsersContextIfActive();
            LOGGER.info("Deleted role: {}", role);
        } else {
            LOGGER.warn("The role {} cant be deleted", role.getRoleName());
        }

    }

    @Override
    @Transactional
    public void createRole(RoleDto role) {
        if (findByRoleName(role.getRoleName()) == null) {
            LOGGER.info("Creating role: {}", role.getRoleName());
            MotechRole motechRole = new MotechRole(role.getRoleName(), role.getPermissionNames(), role.isDeletable());
            motechRolesDataService.create(motechRole);
            userContextsService.refreshAllUsersContextIfActive();
            LOGGER.info("Created role: {}", role.getRoleName());
        } else {
            LOGGER.info("A role with name {} alredy exists. The role has not been added.", role.getRoleName());
        }
    }

    private MotechRole findByRoleName(String roleName) {
        return null == roleName ? null : motechRolesDataService.findByRoleName(roleName);
    }

    @Autowired
    public void setMotechRolesDataService(MotechRolesDataService motechRolesDataService) {
        this.motechRolesDataService = motechRolesDataService;
    }

    @Autowired
    public void setMotechUsersDao(MotechUsersDao motechUsersDao) {
        this.motechUsersDao = motechUsersDao;
    }

    @Autowired
    public void setUserContextsService(UserContextService userContextsService) {
        this.userContextsService = userContextsService;
    }
}
