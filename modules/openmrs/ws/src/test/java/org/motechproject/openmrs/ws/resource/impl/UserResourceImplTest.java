package org.motechproject.openmrs.ws.resource.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Person;
import org.motechproject.openmrs.ws.resource.model.Role;
import org.motechproject.openmrs.ws.resource.model.RoleListResult;
import org.motechproject.openmrs.ws.resource.model.User;
import org.motechproject.openmrs.ws.resource.model.UserListResult;

public class UserResourceImplTest extends AbstractResourceImplTest {

    private UserResourceImpl impl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        impl = new UserResourceImpl(getClient(), getInstance());
    }

    @Test
    public void shouldGetAllUsers() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/user-list-full-response.json"));

        UserListResult result = impl.getAllUsers();

        assertEquals(3, result.getResults().size());
    }

    @Test
    public void shouldQueryForUser() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/user-by-username-response.json"));

        UserListResult result = impl.queryForUsersByUsername("AAA");

        assertEquals(1, result.getResults().size());
    }

    @Test
    public void shouldGetAllRoles() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/role-response.json"));

        RoleListResult result = impl.getAllRoles();

        assertEquals(1, result.getResults().size());
    }

    @Test
    public void shouldCreateUser() throws IOException, HttpException {
        User user = buildUser();

        impl.createUser(user);

        ArgumentCaptor<String> sentJson = ArgumentCaptor.forClass(String.class);
        Mockito.verify(getClient()).postForJson(Mockito.any(URI.class), sentJson.capture());
        String expectedJson = readJsonFromFile("json/user-create.json");

        assertEquals(stringToJsonElement(expectedJson), stringToJsonElement(sentJson.getValue()));
    }

    private User buildUser() {
        User user = new User();
        user.setUsername("motech");
        user.setPassword("password");
        Person person = new Person();
        person.setUuid("personUuid");
        user.setPerson(person);

        Role role = new Role();
        role.setUuid("roleUuid");
        user.setRoles(Arrays.asList(role));

        return user;
    }

    @Test
    public void shouldNotIncludeUuidOnUpdateUser() throws HttpException {
        User user = new User();
        user.setUuid("AAA");
        user.setUsername("motech");

        impl.updateUser(user);

        ArgumentCaptor<String> sentJson = ArgumentCaptor.forClass(String.class);
        Mockito.verify(getClient()).postWithEmptyResponseBody(Mockito.any(URI.class), sentJson.capture());
        String expectedJson = "{\"username\":\"motech\"}";

        assertEquals(stringToJsonElement(expectedJson), stringToJsonElement(sentJson.getValue()));
    }
}
