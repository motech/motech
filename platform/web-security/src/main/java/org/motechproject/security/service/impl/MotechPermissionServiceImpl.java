package org.motechproject.security.service.impl;

import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.mds.MotechPermissionsDataService;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.security.constants.UserRoleNames.MOTECH_ADMIN;

/**
 * Implementation of the {@link org.motechproject.security.service.MotechPermissionService}
 * interface. Uses {@link MotechPermissionsDataService} and {@link MotechPermissionService} in order
 * to retrieve and persist permissions.
 */
@Service("motechPermissionService")
public class MotechPermissionServiceImpl implements MotechPermissionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MotechPermissionServiceImpl.class);

    private MotechPermissionsDataService permissionsDataService;
    private UserContextService userContextsService;
    private MotechRoleService motechRoleService;

    @Override
    @Transactional
    public List<PermissionDto> getPermissions() {
        List<PermissionDto> permissions = new ArrayList<>();
        for (MotechPermission permission : permissionsDataService.retrieveAll()) {
            permissions.add(new PermissionDto(permission));
        }
        return permissions;
    }

    @Override
    @Transactional
    public PermissionDto findPermissionByName(String name) {
        if (name == null) {
            return null;
        }

        MotechPermission motechPermission = permissionsDataService.findByPermissionName(name);

        return motechPermission == null ? null : new PermissionDto(motechPermission);
    }

    @Override
    @Transactional
    public void addPermission(PermissionDto permission) {
        LOGGER.info("Adding permission: {} from bundle: {}", permission.getPermissionName(), permission.getBundleName());

        add(new MotechPermission(permission.getPermissionName(), permission.getBundleName()));

        // the admin role was potentially updated
        userContextsService.refreshAllUsersContextIfActive();

        LOGGER.info("Added permission: {} from bundle: {}", permission.getPermissionName(), permission.getBundleName());
    }

    @Override
    @Transactional
    public void deletePermission(String permissionName) {
        LOGGER.info("Deleting permission: {}", permissionName);
        MotechPermission permission = permissionsDataService.findByPermissionName(permissionName);
        if (permission != null) {
            permissionsDataService.delete(permission);
            removePermissionFromRoles(permissionName);
            userContextsService.refreshAllUsersContextIfActive();
        }
        LOGGER.info("Deleted permission: {}", permissionName);
    }

    private void removePermissionFromRoles(String permissionName) {
        LOGGER.info("Removing permission: {} from roles", permissionName);
        List<RoleDto> roles = motechRoleService.getRoles();
        for (RoleDto role : roles) {
            if (role.hasPermission(permissionName)) {
                role.removePermission(permissionName);
                motechRoleService.updateRole(role);
            }
        }
        LOGGER.info("Removed permission: {} from roles", permissionName);
    }

    private void add(final MotechPermission permission) {
        if (findPermissionByName(permission.getPermissionName()) != null) {
            return;
        }

        permissionsDataService.create(permission);

        RoleDto adminRole = motechRoleService.getRole(MOTECH_ADMIN);
        if (adminRole != null) {
            List<String> permissions = adminRole.getPermissionNames();
            permissions.add(permission.getPermissionName());
            adminRole.setPermissionNames(permissions);
            motechRoleService.updateRole(adminRole);
        }
    }

    @Autowired
    public void setPermissionsDataService(MotechPermissionsDataService permissionsDataService) {
        this.permissionsDataService = permissionsDataService;
    }

    @Autowired
    public void setUserContextsService(UserContextService userContextsService) {
        this.userContextsService = userContextsService;
    }

    @Autowired
    public void setMotechRoleService(MotechRoleService motechRoleService) {
        this.motechRoleService = motechRoleService;
    }
}
