package org.motechproject.security;

import org.apache.commons.io.IOUtils;
import org.ektorp.CouchDbConnector;
import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechPermissionCouchdbImpl;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.motechproject.security.helper.AuthenticationMode;
import org.motechproject.security.repository.AllMotechPermissions;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;


public class Initialize {
    private static final Logger LOGGER = LoggerFactory.getLogger(Initialize.class);
    private static final String WEB_SECURITY = "websecurity";

    @Autowired
    private MotechUserService motechUserService;

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
        Properties properties = getProperties();
        String adminName = properties.getProperty("admin.login");
        String adminPassword = properties.getProperty("admin.password");
        String adminEmail = properties.getProperty("admin.email");
        if (AuthenticationMode.REPOSITORY.equals(properties.getProperty("login.mode")) && null != adminName && null != adminPassword && null != adminEmail) {
           motechUserService.registerAdminUser(adminName, adminPassword, adminEmail, Arrays.asList(adminUser.getRoleName()), true);
        }
    }

    // TODO: get rid of this once we move to platform
    private Properties getProperties() {
        File file = new File(String.format("%s/.motech/config/%s", System.getProperty("user.home"), PlatformSettingsService.SETTINGS_FILE_NAME));
        if (!file.exists()) {
            return null;
        }

        Properties properties = new Properties();
        FileInputStream fileStream = null;

        try {
            fileStream = new FileInputStream(file);
            properties.load(fileStream);
        } catch (IOException e) {
            LOGGER.info("Can not read file." + e);
        } finally {
            IOUtils.closeQuietly(fileStream);
        }

        return properties;
    }
}
