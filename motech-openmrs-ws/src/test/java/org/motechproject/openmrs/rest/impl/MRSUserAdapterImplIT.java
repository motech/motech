package org.motechproject.openmrs.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationOpenMrsWS.xml" })
public class MRSUserAdapterImplIT {

    @Autowired
    MRSUserAdapter userAdapter;

    @Autowired
    AdapterHelper adapterHelper;

    @Test
    public void shouldCreateUser() throws UserAlreadyExistsException, HttpException, URISyntaxException {
        MRSUser user = null;
        try {
            Map values = createUser();

            user = (MRSUser) values.get(MRSUserAdapterImpl.USER_KEY);
            assertNotNull(user);
            assertNotNull(values.get(MRSUserAdapterImpl.PASS_KEY));
            assertNotNull(user.getId());
        } finally {
            adapterHelper.deleteUser(user);
        }
    }

    private Map createUser() throws UserAlreadyExistsException {
        MRSUser user = makeUser();
        Map values = userAdapter.saveUser(user);
        return values;
    }

    private MRSUser makeUser() {
        MRSPerson person = TestUtils.makePerson();
        MRSUser user = new MRSUser();
        user.userName("test_user");
        user.securityRole("Provider");
        user.person(person);

        return user;
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void shouldThrowDuplicateUserException() throws UserAlreadyExistsException, HttpException,
            URISyntaxException {
        MRSUser user = null;
        try {
            user = getCreatedUser();
            createUser();
        } finally {
            adapterHelper.deleteUser(user);
        }
    }

    private MRSUser getCreatedUser() throws UserAlreadyExistsException {
        return (MRSUser) createUser().get(MRSUserAdapterImpl.USER_KEY);
    }

    @Test
    public void shouldFindUser() throws UserAlreadyExistsException, HttpException, URISyntaxException {
        MRSUser originalUser = null;
        try {
            Map values = createUser();
            originalUser = (MRSUser) values.get(MRSUserAdapterImpl.USER_KEY);
            MRSUser searchedUser = userAdapter.getUserByUserName("test_user");

            assertNotNull(searchedUser);
            assertEquals(originalUser.getId(), searchedUser.getId());
            assertEquals(originalUser.getUserName(), searchedUser.getUserName());
            assertEquals(originalUser.getSecurityRole(), searchedUser.getSecurityRole());
            assertEquals(originalUser.getSystemId(), searchedUser.getSystemId());

            MRSPerson originalPerson = originalUser.getPerson();
            MRSPerson searchedPerson = searchedUser.getPerson();

            assertNotNull(searchedPerson);
        } finally {
            adapterHelper.deleteUser(originalUser);
        }
    }

    @Test
    public void shouldUpdateUserPassword() throws UserAlreadyExistsException, HttpException, URISyntaxException {
        MRSUser user = null;
        try {
            Map values = createUser();
            user = (MRSUser) values.get(MRSUserAdapterImpl.USER_KEY);

            String newPassword = userAdapter.setNewPasswordForUser("test_user");

            assertNotNull(newPassword);
            assertFalse(newPassword.equals(values.get(MRSUserAdapterImpl.PASS_KEY).toString()));
        } finally {
            adapterHelper.deleteUser(user);
        }
    }

    @Test
    public void shouldListAllUsers() throws UserAlreadyExistsException, HttpException, URISyntaxException {
        MRSUser user = null;
        try {
            user = getCreatedUser();

            List<MRSUser> users = userAdapter.getAllUsers();

            assertEquals(1, users.size());
        } finally {
            adapterHelper.deleteUser(user);
        }
    }

    @Test
    public void shouldUpdateUser() throws UserAlreadyExistsException, HttpException, URISyntaxException {
        MRSUser user = null;
        try {
            Map values = createUser();
            user = (MRSUser) values.get(MRSUserAdapterImpl.USER_KEY);

            user.getPerson().firstName("Changed");
            user.getPerson().middleName("Changed");
            user.getPerson().lastName("Changed");
            user.getPerson().address("Changed");
            Map updatedValues = userAdapter.updateUser(user);
            MRSUser updatedUser = (MRSUser) values.get(MRSUserAdapterImpl.USER_KEY);

            String originalPassword = (String) values.get(MRSUserAdapterImpl.PASS_KEY);
            String updatedPassword = (String) updatedValues.get(MRSUserAdapterImpl.PASS_KEY);
            MRSPerson updatedPerson = updatedUser.getPerson();

            assertFalse(originalPassword.equals(updatedPassword));
            assertEquals("Changed", updatedPerson.getFirstName());
            assertEquals("Changed", updatedPerson.getMiddleName());
            assertEquals("Changed", updatedPerson.getLastName());
            assertEquals("Changed", updatedPerson.getAddress());
        } finally {
            adapterHelper.deleteUser(user);
        }
    }
}
