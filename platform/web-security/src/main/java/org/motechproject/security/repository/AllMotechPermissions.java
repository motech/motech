package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import java.util.List;

import static org.motechproject.security.constants.UserRoleNames.MOTECH_ADMIN;

/**
 * Implementation of DAO interface that utilizes a MDS back-end for storage.
 * Class responsible for handling MotechPermission.
 */
@Repository
public class AllMotechPermissions {
    private MotechPermissionsDataService dataService;
    private AllMotechRoles allMotechRoles;

    /**
     * Adds new MotechPermission and update Motech Admin role to contain it
     *
     * @param permission to be added
     */
    @Transactional
    public void add(final MotechPermission permission) {
        if (findByPermissionName(permission.getPermissionName()) != null) {
            return;
        }

        dataService.create(permission);

        dataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                MotechRole adminRole = allMotechRoles.findByRoleName(MOTECH_ADMIN);
                if (adminRole != null) {
                    List<String> permissions = adminRole.getPermissionNames();
                    permissions.add(permission.getPermissionName());
                    adminRole.setPermissionNames(permissions);
                    allMotechRoles.update(adminRole);
                }
            }
        });
    }

    /**
     * Returns MotechPermission with given name
     *
     * @param permissionName name of permission
     * @return MotechPermission
     */
    @Transactional
    public MotechPermission findByPermissionName(String permissionName) {
        return null == permissionName ? null : dataService.findByPermissionName(permissionName);
    }

    /**
     * Returns all MotechPermissions
     *
     * @return list that contains permissions
     */
    @Transactional
    public List<MotechPermission> getPermissions() {
        return dataService.retrieveAll();
    }

    /**
     * Deletes given MotechPermission
     *
     * @param permission to be removed
     */
    @Transactional
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
