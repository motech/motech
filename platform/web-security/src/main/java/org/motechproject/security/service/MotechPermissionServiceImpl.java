package org.motechproject.security.service;

import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechPermissionCouchdbImpl;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.repository.AllMotechPermissions;
import org.motechproject.security.repository.AllMotechRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link MotechPermissionService} interface. Uses {@link AllMotechPermissions} in order
 * to retrieve and persist permissions.
 */
@Service("motechPermissionService")
public class MotechPermissionServiceImpl implements MotechPermissionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MotechPermissionServiceImpl.class);

    @Autowired
    private AllMotechPermissions allMotechPermissions;

    @Autowired
    private UserContextService userContextsService;

    @Autowired
    private AllMotechRoles allMotechRoles;

    @Override
    public List<PermissionDto> getPermissions() {
        List<PermissionDto> permissions = new ArrayList<>();
        for (MotechPermission permission : allMotechPermissions.getPermissions()) {
            permissions.add(new PermissionDto(permission));
        }
        return permissions;
    }

    @Override
    public void addPermission(PermissionDto permission) {
        LOGGER.info("Adding permission: {} from bundle: {}", permission.getPermissionName(), permission.getBundleName());
        allMotechPermissions.add(new MotechPermissionCouchdbImpl(permission.getPermissionName(),
                permission.getBundleName()));
        LOGGER.info("Added permission: {} from bundle: {}", permission.getPermissionName(), permission.getBundleName());
    }

    @Override
    public void deletePermission(String permissionName) {
        LOGGER.info("Deleting permission: {}", permissionName);
        MotechPermission permission = allMotechPermissions.findByPermissionName(permissionName);
        if (permission != null) {
            allMotechPermissions.delete(permission);
            removePermissionFromRoles(permissionName);
            userContextsService.refreshAllUsersContextIfActive();
        }
        LOGGER.info("Deleted permission: {}", permissionName);
    }

    private void removePermissionFromRoles(String permissionName) {
        LOGGER.info("Removing permission: {} from roles", permissionName);
        List<MotechRole> roles = allMotechRoles.getRoles();
        for (MotechRole role : roles) {
            if (role.hasPermission(permissionName)) {
                role.removePermission(permissionName);
                allMotechRoles.update(role);
            }
        }
        LOGGER.info("Removed permission: {} from roles", permissionName);
    }

}
