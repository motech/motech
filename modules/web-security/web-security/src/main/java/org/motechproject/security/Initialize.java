package org.motechproject.security;

import org.apache.commons.io.IOUtils;
import org.ektorp.CouchDbConnector;
import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechPermissionCouchdbImpl;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.motechproject.security.repository.AllMotechPermissions;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Properties;


public class Initialize {

    private static Logger logger = LoggerFactory.getLogger(Initialize.class);

    @Autowired
    private MotechUserService motechUserService;

    @Autowired
    private AllMotechPermissions allMotechPermissions;

    @Autowired
    private AllMotechRoles allMotechRoles;

    @Autowired
    public void initialize(@Qualifier("webSecurityDbConnector") CouchDbConnector db) throws IOException {
        //initialize startup permission for admin user
        MotechPermission addUserPerrmision = new MotechPermissionCouchdbImpl("addUser", "websecurity");
        MotechPermission editUserPermission = new MotechPermissionCouchdbImpl("editUser", "websecurity");
        MotechPermission deleteUserPermission = new MotechPermissionCouchdbImpl("deleteUser", "websecurity");
        MotechPermission manageUserPerrmision = new MotechPermissionCouchdbImpl("manageUser", "websecurity");
        MotechPermission activeUserPerrmision = new MotechPermissionCouchdbImpl("activateUser", "websecurity");
        MotechPermission manageRolePermission = new MotechPermissionCouchdbImpl("manageRole", "websecurity");

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

        Properties adminDetails = new Properties();

        InputStream file = null;
        try {
            file = new FileInputStream(String.format("%s/.motech/config/%s", System.getProperty("user.home"), PlatformSettingsService.SETTINGS_FILE_NAME));
            adminDetails.load(new InputStreamReader(file));
            String adminName = adminDetails.getProperty("admin.login");
            String adminPassword = adminDetails.getProperty("admin.password");
            motechUserService.register(adminName, adminPassword, "motech@motech", "", Arrays.asList(adminUser.getRoleName()));
        } catch (IOException e) {
            logger.debug("Can read file", e);
        } finally {
            IOUtils.closeQuietly(file);
        }


    }
}
