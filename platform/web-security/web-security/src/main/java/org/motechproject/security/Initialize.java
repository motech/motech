package org.motechproject.security;

import org.ektorp.CouchDbConnector;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechPermissionCouchdbImpl;
import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.motechproject.security.repository.AllMotechPermissions;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.AllMotechUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This class initializes some of the Motech Permissions, initializes User Admin role, as well as
 * fixes a bug with "Admin User" role name.
 */

public class Initialize {
    private static final String WEB_SECURITY = "websecurity";
    private static final String EMAIL = "email";
    private static final String USER_ADMIN = "User Admin";
    private static final String ADMIN_USER = "Admin User";

    @Autowired
    private AllMotechPermissions allMotechPermissions;

    @Autowired
    private AllMotechRoles allMotechRoles;

    @Autowired
    private AllMotechUsers allMotechUsers;


    @Autowired
    public void initialize(@Qualifier("webSecurityDbConnector") CouchDbConnector db) throws IOException {

        //change role name only if it doesn't exist yet, to prevent multiple roles of the same name
        MotechRole userAdminRole = null;
        userAdminRole = allMotechRoles.findByRoleName(USER_ADMIN);
        if (userAdminRole==null) {
            changeRoleNameToUserAdmin();
        }

        //initialize startup permission for user admin
        MotechPermission addUserPermission = new MotechPermissionCouchdbImpl("addUser", WEB_SECURITY);
        MotechPermission editUserPermission = new MotechPermissionCouchdbImpl("editUser", WEB_SECURITY);
        MotechPermission deleteUserPermission = new MotechPermissionCouchdbImpl("deleteUser", WEB_SECURITY);
        MotechPermission manageUserPermission = new MotechPermissionCouchdbImpl("manageUser", WEB_SECURITY);
        MotechPermission activeUserPermission = new MotechPermissionCouchdbImpl("activateUser", WEB_SECURITY);
        MotechPermission manageRolePermission = new MotechPermissionCouchdbImpl("manageRole", WEB_SECURITY);

        //initialize startup permission for email admin
        MotechPermission viewEmailLogs = new MotechPermissionCouchdbImpl("viewBasicEmailLogs", EMAIL);
        MotechPermission viewDetailedLogs = new MotechPermissionCouchdbImpl("viewDetailedEmailLogs", EMAIL);

        //initialize startup role
        MotechRole userAdmin = new MotechRoleCouchdbImpl(USER_ADMIN, Arrays.asList(addUserPermission.getPermissionName(), editUserPermission.getPermissionName(), deleteUserPermission.getPermissionName(), manageUserPermission.getPermissionName(), activeUserPermission.getPermissionName(), manageRolePermission.getPermissionName()));

        //add created permissions
        allMotechPermissions.add(addUserPermission);
        allMotechPermissions.add(editUserPermission);
        allMotechPermissions.add(deleteUserPermission);
        allMotechPermissions.add(manageUserPermission);
        allMotechPermissions.add(activeUserPermission);
        allMotechPermissions.add(manageRolePermission);
        allMotechPermissions.add(viewEmailLogs);
        allMotechPermissions.add(viewDetailedLogs);

        allMotechRoles.add(userAdmin);
    }

    //This method fixes a bug with role names - renames Admin User role to User Admin, if it exists in the database
    private void changeRoleNameToUserAdmin() {
        MotechRole toChange = null;
        toChange = allMotechRoles.findByRoleName(ADMIN_USER);

        if (toChange != null) {
            toChange.setRoleName(USER_ADMIN);
            allMotechRoles.update(toChange);

            //if exisiting user has got the Admin User role, change it to the proper one
            List<MotechUser> motechUserList = (List<MotechUser>) allMotechUsers.findByRole(ADMIN_USER);

            for (MotechUser user : motechUserList) {
                List<String> roles = user.getRoles();
                roles.remove(ADMIN_USER);
                roles.add(USER_ADMIN);
                user.setRoles(roles);
                allMotechUsers.update(user);
            }
        }
    }

}
