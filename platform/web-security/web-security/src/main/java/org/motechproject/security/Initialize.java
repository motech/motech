package org.motechproject.security;

import org.ektorp.CouchDbConnector;
import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechPermissionCouchdbImpl;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.motechproject.security.repository.AllMotechPermissions;
import org.motechproject.security.repository.AllMotechRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.Arrays;


public class Initialize {
    private static final String WEB_SECURITY = "websecurity";

    @Autowired
    private AllMotechPermissions allMotechPermissions;

    @Autowired
    private AllMotechRoles allMotechRoles;

    @Autowired
    public void initialize(@Qualifier("webSecurityDbConnector") CouchDbConnector db) throws IOException {
        //initialize startup permission for admin user
        MotechPermission addUserPerrmision = new MotechPermissionCouchdbImpl("addUser", WEB_SECURITY);
        MotechPermission editUserPermission = new MotechPermissionCouchdbImpl("editUser", WEB_SECURITY);
        MotechPermission deleteUserPermission = new MotechPermissionCouchdbImpl("deleteUser", WEB_SECURITY);
        MotechPermission manageUserPerrmision = new MotechPermissionCouchdbImpl("manageUser", WEB_SECURITY);
        MotechPermission activeUserPerrmision = new MotechPermissionCouchdbImpl("activateUser", WEB_SECURITY);
        MotechPermission manageRolePermission = new MotechPermissionCouchdbImpl("manageRole", WEB_SECURITY);

        //initialize startup role
        MotechRole adminUser = new MotechRoleCouchdbImpl("Admin User", Arrays.asList(addUserPerrmision.getPermissionName(), editUserPermission.getPermissionName(), deleteUserPermission.getPermissionName(), manageUserPerrmision.getPermissionName(), activeUserPerrmision.getPermissionName(), manageRolePermission.getPermissionName()));

        //create all startup security
        allMotechPermissions.add(addUserPerrmision);
        allMotechPermissions.add(editUserPermission);
        allMotechPermissions.add(deleteUserPermission);
        allMotechPermissions.add(manageUserPerrmision);
        allMotechPermissions.add(activeUserPerrmision);
        allMotechPermissions.add(manageRolePermission);

        allMotechRoles.add(adminUser);
    }

}
