package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.motechproject.security.constants.UserRoleNames.MOTECH_ADMIN;

@Repository
public class AllMotechPermissions {
    private MotechPermissionsDataService dataService;
    private AllMotechRoles allMotechRoles;

    public void add(MotechPermission permission) {
        if (findByPermissionName(permission.getPermissionName()) != null) {
            return;
        }

        dataService.create(permission);
        MotechRole adminRole = allMotechRoles.findByRoleName(MOTECH_ADMIN);
        if (adminRole != null) {
            List<String> permissions = adminRole.getPermissionNames();
            permissions.add(permission.getPermissionName());
            adminRole.setPermissionNames(permissions);
            allMotechRoles.update(adminRole);
        }
    }

    public MotechPermission findByPermissionName(String permissionName) {
        return null == permissionName ? null : dataService.findByPermissionName(permissionName);
    }

    public List<MotechPermission> getPermissions() {
        return dataService.retrieveAll();
    }

    public void delete(MotechPermission permission) {
        dataService.delete(permission);
    }

    @Autowired
    public void setDataService(MotechPermissionsDataService dataService) {
        this.dataService = dataService;
    }

    @Autowired
    public void setAllMotechRoles(AllMotechRoles allMotechRoles) {
        this.allMotechRoles = allMotechRoles;
    }
}
