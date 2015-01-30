package org.motechproject.security;

import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.repository.AllMotechPermissions;
import org.motechproject.security.repository.AllMotechRoles;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

import static org.motechproject.security.constants.PermissionNames.ACTIVATE_USER_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.ADD_USER_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.DELETE_USER_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.EDIT_USER_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MANAGE_PERMISSION_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MANAGE_ROLE_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MANAGE_USER_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MDS_DATA_ACCESS_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MDS_SCHEMA_ACCESS_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MDS_SETTINGS_ACCESS_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.UPDATE_SECURITY_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.VIEW_BASIC_EMAIL_LOGS_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.VIEW_DETAILED_EMAIL_LOGS_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.VIEW_SECURITY;
import static org.motechproject.security.constants.UserRoleNames.MOTECH_ADMIN;

/**
 * This class initializes some of the Motech Permissions, initializes User Admin role, as well as
 * fixes a bug with "Admin User" role name.
 */

public class Initialize {
    private static final String WEB_SECURITY = "org.motechproject.motech-platform-web-security";
    private  static final String MDS_MODULE = "org.motechproject.motech-platform-dataservices";
    private static final String EMAIL = "org.motechproject.motech-platform-email";

    private AllMotechPermissions allMotechPermissions;
    private AllMotechRoles allMotechRoles;

    /**
     * Initializes module by creating Motech Admin role
     * and permissions
     */
    @PostConstruct
    public void initialize() {
        //Create Motech Admin role
        if (allMotechRoles.findByRoleName(MOTECH_ADMIN) == null) {
            List<String> permissionsNames = new LinkedList<>();
            List<MotechPermission> permissions = allMotechPermissions.getPermissions();
            for (MotechPermission permission : permissions) {
                permissionsNames.add(permission.getPermissionName());
            }
            MotechRole adminRole = new MotechRole(MOTECH_ADMIN, permissionsNames, false);
            allMotechRoles.add(adminRole);
        }
        //initialize startup permission for Admin role
        prepareStartupPermissions();
    }

    /**
     * Creates and adds all necessary startup permissions
     */
    private void prepareStartupPermissions() {
        //Web Security module
        MotechPermission addUserPermission = new MotechPermission(ADD_USER_PERMISSION, WEB_SECURITY);
        MotechPermission editUserPermission = new MotechPermission(EDIT_USER_PERMISSION, WEB_SECURITY);
        MotechPermission deleteUserPermission = new MotechPermission(DELETE_USER_PERMISSION, WEB_SECURITY);
        MotechPermission manageUserPermission = new MotechPermission(MANAGE_USER_PERMISSION, WEB_SECURITY);
        MotechPermission activeUserPermission = new MotechPermission(ACTIVATE_USER_PERMISSION, WEB_SECURITY);
        MotechPermission manageRolePermission = new MotechPermission(MANAGE_ROLE_PERMISSION, WEB_SECURITY);
        MotechPermission managePermissionPermission = new MotechPermission(MANAGE_PERMISSION_PERMISSION, WEB_SECURITY);
        MotechPermission viewSecurity = new MotechPermission(VIEW_SECURITY, WEB_SECURITY);
        MotechPermission updateSecurity = new MotechPermission(UPDATE_SECURITY_PERMISSION, WEB_SECURITY);
        allMotechPermissions.add(addUserPermission);
        allMotechPermissions.add(editUserPermission);
        allMotechPermissions.add(deleteUserPermission);
        allMotechPermissions.add(manageUserPermission);
        allMotechPermissions.add(activeUserPermission);
        allMotechPermissions.add(manageRolePermission);
        allMotechPermissions.add(managePermissionPermission);
        allMotechPermissions.add(viewSecurity);
        allMotechPermissions.add(updateSecurity);

        //MDS module
        MotechPermission mdsSchemaAccess = new MotechPermission(MDS_SCHEMA_ACCESS_PERMISSION, MDS_MODULE);
        MotechPermission mdsSettingsAccess = new MotechPermission(MDS_SETTINGS_ACCESS_PERMISSION, MDS_MODULE);
        MotechPermission mdsDataAccess = new MotechPermission(MDS_DATA_ACCESS_PERMISSION, MDS_MODULE);
        allMotechPermissions.add(mdsSchemaAccess);
        allMotechPermissions.add(mdsSettingsAccess);
        allMotechPermissions.add(mdsDataAccess);

        //Email module
        MotechPermission viewEmailLogs = new MotechPermission(VIEW_BASIC_EMAIL_LOGS_PERMISSION, EMAIL);
        MotechPermission viewDetailedLogs = new MotechPermission(VIEW_DETAILED_EMAIL_LOGS_PERMISSION, EMAIL);
        allMotechPermissions.add(viewEmailLogs);
        allMotechPermissions.add(viewDetailedLogs);
    }

    @Autowired
    public void setAllMotechPermissions(AllMotechPermissions allMotechPermissions) {
        this.allMotechPermissions = allMotechPermissions;
    }

    @Autowired
    public void setAllMotechRoles(AllMotechRoles allMotechRoles) {
        this.allMotechRoles = allMotechRoles;
    }

}
