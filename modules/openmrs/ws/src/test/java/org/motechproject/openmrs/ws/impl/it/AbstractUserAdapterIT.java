package org.motechproject.openmrs.ws.impl.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractUserAdapterIT {

    @Autowired
    private MRSUserAdapter userAdapter;

    @Test
    public void shouldCreateUser() throws UserAlreadyExistsException {
        MRSPerson person = new MRSPerson().firstName("Denise").lastName("Test").address("10 Fifth Avenue")
                .birthDateEstimated(false).gender("M").preferredName("Denise");
        MRSUser user = new MRSUser();
        user.securityRole("Provider");
        user.userName("denise");
        user.person(person);

        Map<String, Object> result = userAdapter.saveUser(user);
        MRSUser saved = (MRSUser) result.get(MRSUserAdapter.USER_KEY);

        assertNotNull(saved);
        assertNotNull(saved.getId());
    }

    @Test
    public void shouldSetNewPassword() throws UserAlreadyExistsException {
        String newPassword = userAdapter.setNewPasswordForUser("chuck");

        assertNotNull(newPassword);
    }

    @Test
    public void shouldGetUserByUsername() throws UserAlreadyExistsException {
        MRSUser user = userAdapter.getUserByUserName("chuck");

        assertNotNull(user);
    }

    @Test
    public void shouldGetAllUsers() throws UserAlreadyExistsException {
        List<MRSUser> users = userAdapter.getAllUsers();

        assertTrue(users.size() > 0);
    }

    @Test
    public void shouldUpdateUser() throws UserAlreadyExistsException {
        MRSUser saved = userAdapter.getUserByUserName("chuck");
        saved.getPerson().firstName("John2");
        userAdapter.updateUser(saved);

        MRSUser updated = userAdapter.getUserByUserName("john2");

        assertNotNull(updated);
        assertEquals("John2", updated.getPerson().getFirstName());
    }
}
