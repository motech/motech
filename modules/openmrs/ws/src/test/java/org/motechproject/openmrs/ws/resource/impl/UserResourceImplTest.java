package org.motechproject.openmrs.ws.resource.impl;

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

import java.io.IOException;
import java.net.URI;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

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

        assertEquals(asList("b187e426-4d07-11e1-a4ea-00ff26c46bb6", "A4F30A1B-5EB9-11DF-A648-37A07F9C90FB", "1752391c-1e30-4682-b699-e3dcab79d4d3"),
                extract(result.getResults(), on(User.class).getUuid()));
    }

    @Test
    public void shouldQueryForUser() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/user-by-username-response.json"));

        UserListResult result = impl.queryForUsersByUsername("AAA");

        assertEquals(asList("UUU"), extract(result.getResults(), on(User.class).getUuid()));
    }

    @Test
    public void shouldGetAllRoles() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/role-response.json"));

        RoleListResult result = impl.getAllRoles();

        assertEquals(asList("roleUuid"), extract(result.getResults(), on(Role.class).getUuid()));
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
        user.setRoles(asList(role));

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
