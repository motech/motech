package org.motechproject.openmrs.ws.impl.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.domain.User;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.model.OpenMRSUser;
import org.motechproject.mrs.services.UserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractUserAdapterIT {

    @Autowired
    private UserAdapter userAdapter;

    @Test
    public void shouldCreateUser() throws UserAlreadyExistsException {
        OpenMRSPerson person = new OpenMRSPerson().firstName("Denise").lastName("Test").address("10 Fifth Avenue")
                .birthDateEstimated(false).gender("M").preferredName("Denise");
        OpenMRSUser user = new OpenMRSUser();
        user.securityRole("Provider");
        user.userName("denise");
        user.person(person);

        Map<String, Object> result = userAdapter.saveUser(user);
        OpenMRSUser saved = (OpenMRSUser) result.get(UserAdapter.USER_KEY);

        assertNotNull(saved);
        assertNotNull(saved.getUserId());
    }

    @Test
    public void shouldSetNewPassword() throws UserAlreadyExistsException {
        String newPassword = userAdapter.setNewPasswordForUser("chuck");

        assertNotNull(newPassword);
    }

    @Test
    public void shouldGetUserByUsername() throws UserAlreadyExistsException {
        OpenMRSUser user = (OpenMRSUser) userAdapter.getUserByUserName("chuck");

        assertNotNull(user);
    }

    @Test
    public void shouldGetAllUsers() throws UserAlreadyExistsException {
        List<User> users = userAdapter.getAllUsers();

        assertTrue(users.size() > 0);
    }

    @Test
    public void shouldUpdateUser() throws UserAlreadyExistsException {
        OpenMRSUser saved = (OpenMRSUser) userAdapter.getUserByUserName("chuck");
        saved.getPerson().firstName("John2");
        userAdapter.updateUser(saved);

        OpenMRSUser updated = (OpenMRSUser) userAdapter.getUserByUserName("john2");

        assertNotNull(updated);
        assertEquals("John2", updated.getPerson().getFirstName());
    }
}
