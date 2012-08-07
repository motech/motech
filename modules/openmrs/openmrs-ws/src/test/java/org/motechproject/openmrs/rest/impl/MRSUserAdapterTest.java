package org.motechproject.openmrs.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.google.gson.JsonElement;

public class MRSUserAdapterTest {

    @Mock
    private MRSPersonAdapterImpl personAdapter;

    @Mock
    private RestClient client;

    @Mock
    private OpenMrsUrlHolder urlHolder;

    private MRSUserAdapterImpl impl;

    @Before
    public void setUp() {
        initMocks(this);
        impl = new MRSUserAdapterImpl(personAdapter, client, urlHolder);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowUnsupportedExceptionOnChangeCurrentPassword() {
        impl.changeCurrentUserPassword(null, null);
    }

    @Test
    public void shouldFilterAdminDaemonFromAllUsersList() throws IOException, HttpException {
        String json = TestUtils.parseJsonFileAsString("json/user-list-full-response.json");

        when(client.getJson(null)).thenReturn(json);

        List<MRSUser> users = impl.getAllUsers();

        assertEquals(1, users.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullUsername() {
        impl.getUserByUserName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnEmptyUsername() {
        impl.getUserByUserName("");
    }

    @Test
    public void shouldReturnNullOnUserNotFound() throws HttpException {
        when(client.getJson(null)).thenReturn("{\"results\":[]}");
        MRSUser user = impl.getUserByUserName("some name");

        assertNull(user);
    }

    @Test
    public void shouldGetUserByUsername() throws IOException, HttpException {
        String json = TestUtils.parseJsonFileAsString("json/user-by-username-response.json");

        when(client.getJson(null)).thenReturn(json);
        when(personAdapter.getPerson("PPP")).thenReturn(null);

        MRSUser user = impl.getUserByUserName("username");
        MRSUser expected = makeExpectedUser();
        assertEquals(expected, user);
    }

    private MRSUser makeExpectedUser() {
        return new MRSUser().id("UUU").userName("test_user").systemId("3-4").person(null).securityRole("Provider");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullUser() throws UserAlreadyExistsException {
        impl.saveUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullUsernameOnSave() throws UserAlreadyExistsException {
        impl.saveUser(new MRSUser().userName(null));
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void shouldThrowExceptionOnUserAlreadyExist() throws IOException, HttpException, UserAlreadyExistsException {
        String userJson = TestUtils.parseJsonFileAsString("json/user-by-username-response.json");
        when(client.getJson(null)).thenReturn(userJson);

        impl.saveUser(new MRSUser().userName("bob").person(new MRSPerson()));
    }

    @Test
    public void shouldSaveUser() throws IOException, HttpException, UserAlreadyExistsException {
        String roleJson = TestUtils.parseJsonFileAsString("json/role-response.json");
        MRSPerson person = new MRSPerson();

        when(client.getJson(null)).thenReturn("{\"results\":[]}").thenReturn(roleJson);
        when(personAdapter.savePerson(person)).thenReturn(new MRSPerson().id("personUuid"));
        when(client.postForJson(any(URI.class), any(String.class))).thenReturn("{}");

        MRSUser user = new MRSUser();
        user.person(person).userName("theUsername").securityRole("Provider");
        Map results = impl.saveUser(user);
        String password = results.get(MRSUserAdapter.PASSWORD_KEY).toString();

        ArgumentCaptor<String> sentJson = ArgumentCaptor.forClass(String.class);
        verify(client).postForJson(any(URI.class), sentJson.capture());

        JsonElement expected = TestUtils.parseJsonFile("json/user.json");
        // manually setting password as it is generated in the save method
        expected.getAsJsonObject().addProperty("password", password);

        JsonElement result = TestUtils.parseJsonString(sentJson.getValue());

        assertEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenSettingPasswordWithNullUsername() {
        impl.setNewPasswordForUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenSettingPasswordWithEmptyUsername() {
        impl.setNewPasswordForUser("");
    }

    @Test(expected = UsernameNotFoundException.class)
    public void shouldThrowExceptionWithNoUserFound() throws HttpException {
        when(client.getJson(null)).thenReturn("{\"results\":[]}");
        impl.setNewPasswordForUser("bob");
    }

    @Test
    public void shouldSendJsonForUpdatePassword() throws IOException, HttpException {
        String json = TestUtils.parseJsonFileAsString("json/user-by-username-response.json");

        when(client.getJson(null)).thenReturn(json);
        when(personAdapter.getPerson("PPP")).thenReturn(new MRSPerson().id("PPP"));

        String password = impl.setNewPasswordForUser("bob");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(client).postWithEmptyResponseBody(any(URI.class), captor.capture());

        JsonElement expected = TestUtils
                .parseJsonString("{\"username\":\"test_user\", \"person\":\"PPP\", \"password\":\"" + password + "\"}");
        JsonElement result = TestUtils.parseJsonString(captor.getValue());

        assertEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnUpdateWithNullUser() {
        impl.updateUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnUpdateWithUserNullId() {
        impl.updateUser(new MRSUser().id(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnUpdateWithUserEmptyId() {
        impl.updateUser(new MRSUser().id(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnUpdateWithPersonNullId() {
        MRSPerson person = new MRSPerson().id(null);
        impl.updateUser(new MRSUser().id("a").person(person));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnUpdateWithPersonEmptyId() {
        MRSPerson person = new MRSPerson().id("");
        impl.updateUser(new MRSUser().id("a").person(person));
    }

    @Test
    public void shouldUpdateUser() throws IOException, HttpException {
        String roleJson = TestUtils.parseJsonFileAsString("json/role-response.json");
        MRSPerson person = new MRSPerson().id("personUuid");

        when(client.getJson(null)).thenReturn(roleJson);
        when(personAdapter.savePerson(person)).thenReturn(new MRSPerson().id("personUuid"));

        MRSUser user = new MRSUser().id("AAA");
        user.person(person).userName("theUsername").securityRole("Provider");
        Map<String, Object> results = impl.updateUser(user);
        String password = results.get(MRSUserAdapter.PASSWORD_KEY).toString();

        ArgumentCaptor<String> sentJson = ArgumentCaptor.forClass(String.class);
        verify(client).postWithEmptyResponseBody(any(URI.class), sentJson.capture());

        JsonElement expected = TestUtils.parseJsonFile("json/user.json");
        // manually setting password as it is generated in the save method
        expected.getAsJsonObject().addProperty("password", password);

        JsonElement result = TestUtils.parseJsonString(sentJson.getValue());

        assertEquals(expected, result);
    }
}
