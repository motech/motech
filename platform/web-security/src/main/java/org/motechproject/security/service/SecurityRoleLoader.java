package org.motechproject.security.service;

import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.security.model.RoleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SecurityRoleLoader {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityRoleLoader.class);

    private MotechJsonReader motechJsonReader = new MotechJsonReader();

    private MotechRoleService roleService;

    public SecurityRoleLoader(MotechRoleService roleService) {
        this.roleService = roleService;
    }

    public void loadRoles(ApplicationContext applicationContext) {
        Resource rolesResource = applicationContext.getResource("roles.json");

        if (rolesResource.exists()) {
            try (InputStream in = rolesResource.getInputStream()) {
                List<RoleDto> roles = (List<RoleDto>)
                        motechJsonReader.readFromStream(in, new TypeToken<List<RoleDto>>() { } .getType());

                for (RoleDto role : roles) {
                    RoleDto existingRole = roleService.getRole(role.getRoleName());

                    if (existingRole == null) {
                        roleService.createRole(role);
                    } else {
                        existingRole.setPermissionNames(role.getPermissionNames());
                        roleService.updateRole(existingRole);
                    }
                }
            } catch (IOException e) {
                LOG.error("Unable to read roles in " + applicationContext.getDisplayName(), e);
            }
        }
    }
}
