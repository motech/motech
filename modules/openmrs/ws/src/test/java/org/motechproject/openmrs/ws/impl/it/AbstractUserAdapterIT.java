package org.motechproject.openmrs.ws.impl.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.domain.MRSUser;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.motechproject.openmrs.model.OpenMRSPerson;
import org.motechproject.openmrs.model.OpenMRSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractUserAdapterIT {

    @Autowired
    private MRSUserAdapter userAdapter;

    @Test
    public void shouldCreateUser() throws UserAlreadyExistsException {
        OpenMRSPerson person = new OpenMRSPerson().firstName("Denise").lastName("Test").address("10 Fifth Avenue")
                .birthDateEstimated(false).gender("M").preferredName("Denise");
        OpenMRSUser user = new OpenMRSUser();
        user.securityRole("Provider");
        user.userName("denise");
        user.person(person);

        Map<String, Object> result = userAdapter.saveUser(user);
        OpenMRSUser saved = (OpenMRSUser) result.get(MRSUserAdapter.USER_KEY);

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
        List<MRSUser> users = userAdapter.getAllUsers();

        assertTrue(users.size() > 0);
    }

    @Test
    public void shouldUpdateUser() throws UserAlreadyExistsException {
        OpenMRSUser saved = (OpenMRSUser) userAdapter.getUserByUserName("chuck");
        OpenMRSPerson person = (OpenMRSPerson)saved.getPerson();
        person.firstName("John2");
        userAdapter.updateUser(saved);

        OpenMRSUser updated = (OpenMRSUser) userAdapter.getUserByUserName("john2");

        assertNotNull(updated);
        assertEquals("John2", updated.getPerson().getFirstName());
    }
}
