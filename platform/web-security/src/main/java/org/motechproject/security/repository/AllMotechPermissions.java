package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllMotechPermissions {
    private MotechPermissionsDataService dataService;

    public void add(MotechPermission permission) {
        if (findByPermissionName(permission.getPermissionName()) != null) {
            return;
        }

        dataService.create(permission);
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
}
