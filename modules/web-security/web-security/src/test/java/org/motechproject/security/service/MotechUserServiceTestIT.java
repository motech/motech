package org.motechproject.security.service;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.repository.AllMotechUsersCouchdbImpl;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class MotechUserServiceTestIT extends SpringIntegrationTest {

    @Autowired
    MotechUserService motechUserService;

    @Autowired
    AllMotechUsers allMotechUsers;

    @Autowired
    @Qualifier("webSecurityDbConnector")
    CouchDbConnector connector;

    @Autowired
    private MotechPasswordEncoder passwordEncoder;

    @Test
    public void testRegister() {
        motechUserService.register("userName", "password", "1234", asList("IT_ADMIN", "DB_ADMIN"));
        MotechUser motechUser = allMotechUsers.findByUserName("userName");

        assertNotNull(motechUser);

        assertTrue(motechUser.getRoles().contains("IT_ADMIN"));
        assertTrue(motechUser.getRoles().contains("DB_ADMIN"));
    }

    @Test
    public void shouldActivateUser() {
        motechUserService.register("userName", "password", "1234", asList("IT_ADMIN", "DB_ADMIN"), false);
        motechUserService.activateUser("userName");
        MotechUser motechUser = allMotechUsers.findByUserName("userName");

        assertTrue(motechUser.isActive());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfUserNameIsEmptyForRegister() {
        motechUserService.register("", "password", "ext_id", new ArrayList<String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfUserNameIsEmptyForRegisterWithActiveInfo() {
        motechUserService.register("", "password", "ext_id", new ArrayList<String>(), true);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfPasswordIsEmptyForRegister() {
        motechUserService.register("user", "", "ext_id", new ArrayList<String>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfUserNameisNull() {
        motechUserService.register(null, "", "ext_id", new ArrayList<String>());
    }

    @Test
    public void shouldNotActivateInvalidUser() {
        motechUserService.register("userName", "password", "1234", asList("IT_ADMIN", "DB_ADMIN"), false);
        motechUserService.activateUser("userName1");
        MotechUser motechUser = allMotechUsers.findByUserName("userName");

        assertFalse(motechUser.isActive());
    }

    @Test
    public void shouldCreateActiveUserByDefault() {
        motechUserService.register("userName", "password", "1234", asList("IT_ADMIN", "DB_ADMIN"));
        MotechUser motechUser = allMotechUsers.findByUserName("userName");

        assertTrue(motechUser.isActive());
    }

    @Test
    public void shouldCreateInActiveUser() {
        motechUserService.register("userName", "password", "1234", asList("IT_ADMIN", "DB_ADMIN"), false);
        MotechUser motechUser = allMotechUsers.findByUserName("userName");

        assertFalse(motechUser.isActive());
    }

    @Test
    public void testPasswordEncoding() {
        String plainTextPassword = "testpassword";
        motechUserService.register("testuser", plainTextPassword, "entity1", asList("ADMIN"));
        MotechUser motechUser = allMotechUsers.findByUserName("testuser");
        assertTrue(passwordEncoder.isPasswordValid(motechUser.getPassword(), plainTextPassword));
    }

    @Test
    public void shouldChangePassword() {
        motechUserService.register("userName", "password", "1234", asList("IT_ADMIN", "DB_ADMIN"));
        motechUserService.changePassword("userName", "password", "newPassword");
        MotechUser motechUser = allMotechUsers.findByUserName("userName");
        assertTrue(passwordEncoder.isPasswordValid(motechUser.getPassword(), "newPassword"));
    }

    @Test
    public void shouldNotChangePasswordWithoutOldPassword() {
        motechUserService.register("userName", "password", "1234", asList("IT_ADMIN", "DB_ADMIN"));
        motechUserService.changePassword("userName", "foo", "newPassword");
        MotechUser motechUser = allMotechUsers.findByUserName("userName");
        assertTrue(passwordEncoder.isPasswordValid(motechUser.getPassword(), "password"));
    }

    @Test
    public void hasUserShouldReturnTrueOnlyIfUserExists() {
        assertFalse(motechUserService.hasUser("username"));
        motechUserService.register("userName", "password", "1234", Arrays.asList("IT_ADMIN", "DB_ADMIN"));
        assertTrue(motechUserService.hasUser("username"));
    }

    @Test
    public void shouldValidateUserCredentials() {
        motechUserService.register("username", "password", "1234", asList("IT_ADMIN"));
        assertNotNull(motechUserService.retrieveUserByCredentials("username", "password"));
        assertNull(motechUserService.retrieveUserByCredentials("username", "passw550rd"));
    }

    @After
    public void tearDown() {
        ((AllMotechUsersCouchdbImpl) allMotechUsers).removeAll();
        super.tearDown();
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }
}
