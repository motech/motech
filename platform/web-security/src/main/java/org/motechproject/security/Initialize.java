package org.motechproject.security;

import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.mds.MotechPermissionsDataService;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

import static org.motechproject.security.constants.PermissionNames.MANAGE_ROLE_AND_PERMISSION_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MANAGE_URL_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MANAGE_USER_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MDS_DATA_ACCESS_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MDS_SCHEMA_ACCESS_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MDS_SETTINGS_ACCESS_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.VIEW_BASIC_EMAIL_LOGS_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.VIEW_DETAILED_EMAIL_LOGS_PERMISSION;
import static org.motechproject.security.constants.UserRoleNames.MOTECH_ADMIN;

/**
 * This class initializes some of the Motech Permissions and the MOTECH Admin role.
 */
public class Initialize {
    private static final String WEB_SECURITY = "org.motechproject.motech-platform-web-security";
    private static final String MDS_MODULE = "org.motechproject.motech-platform-dataservices";
    private static final String EMAIL = "org.motechproject.motech-platform-email";

    private MotechRoleService motechRoleService;
    private MotechPermissionsDataService permissionsDataService;
    private MotechPermissionService permissionService;

    /**
     * Initializes module by creating MOTECH Admin role and permissions
     */
    @PostConstruct
    public void initialize() {
        //Create MOTECH Admin role
        if (motechRoleService.getRole(MOTECH_ADMIN) == null) {
            List<String> permissionsNames = new LinkedList<>();
            List<MotechPermission> permissions = permissionsDataService.retrieveAll();
            for (MotechPermission permission : permissions) {
                permissionsNames.add(permission.getPermissionName());
            }
            RoleDto adminRole = new RoleDto(MOTECH_ADMIN, permissionsNames, false);
            motechRoleService.createRole(adminRole);
        }
        //initialize startup permission for Admin role
        prepareStartupPermissions();
    }

    /**
     * Creates and adds all necessary startup permissions
     */
    private void prepareStartupPermissions() {
        //Web Security module
        PermissionDto manageUserPermission = new PermissionDto(MANAGE_USER_PERMISSION, WEB_SECURITY);
        PermissionDto manageRoleAndPermissionPermission = new PermissionDto(MANAGE_ROLE_AND_PERMISSION_PERMISSION, WEB_SECURITY);
        PermissionDto manageURLPermission = new PermissionDto(MANAGE_URL_PERMISSION, WEB_SECURITY);

        permissionService.addPermission(manageUserPermission);
        permissionService.addPermission(manageRoleAndPermissionPermission);
        permissionService.addPermission(manageURLPermission);

        //MDS module
        PermissionDto mdsSchemaAccess = new PermissionDto(MDS_SCHEMA_ACCESS_PERMISSION, MDS_MODULE);
        PermissionDto mdsSettingsAccess = new PermissionDto(MDS_SETTINGS_ACCESS_PERMISSION, MDS_MODULE);
        PermissionDto mdsDataAccess = new PermissionDto(MDS_DATA_ACCESS_PERMISSION, MDS_MODULE);
        permissionService.addPermission(mdsSchemaAccess);
        permissionService.addPermission(mdsSettingsAccess);
        permissionService.addPermission(mdsDataAccess);

        //Email module
        PermissionDto viewEmailLogs = new PermissionDto(VIEW_BASIC_EMAIL_LOGS_PERMISSION, EMAIL);
        PermissionDto viewDetailedLogs = new PermissionDto(VIEW_DETAILED_EMAIL_LOGS_PERMISSION, EMAIL);
        permissionService.addPermission(viewEmailLogs);
        permissionService.addPermission(viewDetailedLogs);
    }

    @Autowired
    public void setMotechRoleService(MotechRoleService motechRoleService) {
        this.motechRoleService = motechRoleService;
    }

    @Autowired
    public void setPermissionsDataService(MotechPermissionsDataService permissionsDataService) {
        this.permissionsDataService = permissionsDataService;
    }

    @Autowired
    public void setPermissionService(MotechPermissionService permissionService) {
        this.permissionService = permissionService;
    }
}
