package org.motechproject.security.service.impl;

import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Helper class that scans an application context
 * for Motech roles
 */
public class SecurityRoleLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityRoleLoader.class);

    private MotechJsonReader motechJsonReader = new MotechJsonReader();

    private MotechRoleService roleService;
    private MotechPermissionService permissionService;

    public SecurityRoleLoader(MotechRoleService roleService, MotechPermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    /**
     * Loads from roles.json file and adds or update them using
     * {@link org.motechproject.security.service.MotechRoleService}
     *
     * @param applicationContext in which file with roles can be found
     */
    public void loadRoles(ApplicationContext applicationContext) {
        LOGGER.info("Loading roles from: {}", applicationContext.getDisplayName());

        Resource rolesResource = applicationContext.getResource("roles.json");

        if (rolesResource.exists()) {
            LOGGER.debug("File roles.json exists in {}", applicationContext.getDisplayName());

            try (InputStream in = rolesResource.getInputStream()) {
                List<RoleDto> roles = (List<RoleDto>)
                        motechJsonReader.readFromStream(in, new TypeToken<List<RoleDto>>() {
                        }.getType());

                for (RoleDto role : roles) {
                    RoleDto existingRole = roleService.getRole(role.getRoleName());

                    if (existingRole == null) {
                        roleService.createRole(role);
                    } else if (roleNeedUpdate(existingRole, role)) {
                        existingRole.setPermissionNames(role.getPermissionNames());
                        roleService.updateRole(existingRole);
                    }

                    savePermissions(role.getPermissionNames(), getSymbolicName(applicationContext));
                }
            } catch (IOException e) {
                LOGGER.error("Unable to read roles in " + applicationContext.getDisplayName(), e);
            }
        }

        LOGGER.info("Loaded roles from: {}", applicationContext.getDisplayName());
    }

    private boolean roleNeedUpdate(RoleDto existingRole, RoleDto role) {
        List<String> oldPermissions = existingRole.getPermissionNames();
        List<String> newPermissions = role.getPermissionNames();
        if (oldPermissions.size() != newPermissions.size()) {
            return true;
        }
        if (!oldPermissions.containsAll(newPermissions)) {
            return true;
        }
        return false;
    }

    private void savePermissions(List<String> permissionNames, String moduleName) {
        for (String permissionName : permissionNames) {
            permissionService.addPermission(new PermissionDto(permissionName, moduleName));
        }
    }

    private String getSymbolicName(ApplicationContext applicationContext) {
        BundleContext bundleContext = applicationContext.getBean(BundleContext.class);
        return bundleContext == null ? null : bundleContext.getBundle().getSymbolicName();
    }
}
