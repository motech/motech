package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSException;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.db.DAOException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSUserAdaptorTest {
    @Mock
    private UserService mockUserService;
    @Mock
    private PersonService mockPersonService;

    private OpenMRSUserAdaptor openMrsUserAdaptor;

    @Before
    public void setUp() {
        initMocks(this);
        openMrsUserAdaptor = new OpenMRSUserAdaptor(mockUserService, mockPersonService);
    }

    @Test
    public void testChangeCurrentUserPassword() throws Exception {
        openMrsUserAdaptor.changeCurrentUserPassword("p1", "p2");
        verify(mockUserService).changePassword("p1", "p2");
    }

    @Test(expected = MRSException.class)
    public void testChangeCurrentUserPasswordFailed() throws Exception {
        doThrow(mock(DAOException.class)).when(mockUserService).changePassword("p1", "p2");
        new OpenMRSUserAdaptor(mockUserService, mockPersonService).changeCurrentUserPassword("p1", "p2");
    }

    @Test
    public void shouldSaveANewUser() throws UserAlreadyExistsException {
        MRSUser mrsUser = new MRSUser();
        Role role = new Role();

        when(mockUserService.getRole(mrsUser.getSecurityRole())).thenReturn(role);
        PersonAttributeType phoneAttribute = mock(PersonAttributeType.class);
        PersonAttributeType staffTypeAttribute = mock(PersonAttributeType.class);
        PersonAttributeType emailAttribute = mock(PersonAttributeType.class);

        when(mockPersonService.getPersonAttributeTypeByName("Staff Type")).thenReturn(staffTypeAttribute);
        when(mockPersonService.getPersonAttributeTypeByName("Phone Number")).thenReturn(phoneAttribute);
        when(mockPersonService.getPersonAttributeTypeByName("Email")).thenReturn(emailAttribute);
        org.openmrs.User expectedUser = mock(org.openmrs.User.class);
        when(mockUserService.saveUser(Matchers.<org.openmrs.User>any(), anyString())).thenReturn(expectedUser);
        final String userName = "user@jackdaniels.com";
        final String firstName = "Jack";
        final String middleName = "H";
        final String lastName = "Daniels";
        final String securityRole = "provider";
        final String staffType = "FA";
        final String phoneNumber = "012345";
        final String email = "jack@daniels.com";
        mrsUser.firstName(firstName).middleName(middleName).lastName(lastName).securityRole(securityRole).userName(userName);
        mrsUser.addAttribute(new Attribute("Staff Type", staffType));
        mrsUser.addAttribute(new Attribute("Phone Number", phoneNumber));
        mrsUser.addAttribute(new Attribute("Email", email));

        Map actualSavedParams = openMrsUserAdaptor.saveUser(mrsUser);

        ArgumentCaptor<org.openmrs.User> captor = ArgumentCaptor.forClass(org.openmrs.User.class);
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockUserService).saveUser(captor.capture(), passwordCaptor.capture());
        String capturedPassword = passwordCaptor.getValue();
        assertEquals(8, capturedPassword.length());

        org.openmrs.User capturedUser = captor.getValue();
        Person person = capturedUser.getPerson();
        assertEquals(firstName, person.getGivenName());
        assertEquals(middleName, person.getMiddleName());
        assertEquals(lastName, person.getFamilyName());
        assertEquals(staffType, person.getAttribute(staffTypeAttribute).getValue());
        assertEquals(phoneNumber, person.getAttribute(phoneAttribute).getValue());
        assertEquals(email, person.getAttribute(emailAttribute).getValue());
        assertEquals("?", person.getGender());
        assertEquals(userName, capturedUser.getUsername());
        assertEquals(expectedUser, actualSavedParams.get("openMRSUser"));
        assertNotNull(actualSavedParams.get("password"));
    }

    @Test
    @Ignore
    public void shouldResetPasswordGivenUserEmailId() throws UsernameNotFoundException, UserAlreadyExistsException {
        MRSUser mrsUser = new MRSUser();
        Role role = new Role();
        final String emailId = "jack@daniels.com";

        when(mockUserService.getRole(mrsUser.getSecurityRole())).thenReturn(role);
        PersonAttributeType phoneAttribute = mock(PersonAttributeType.class);
        PersonAttributeType staffTypeAttribute = mock(PersonAttributeType.class);
        PersonAttributeType emailAttribute = mock(PersonAttributeType.class);

        when(mockPersonService.getPersonAttributeTypeByName("Staff Type")).thenReturn(staffTypeAttribute);
        when(mockPersonService.getPersonAttributeTypeByName("Phone Number")).thenReturn(phoneAttribute);
        when(mockPersonService.getPersonAttributeTypeByName("Email")).thenReturn(emailAttribute);

        mrsUser.firstName("Jack").middleName("H").lastName("Daniels").securityRole("provider");
        mrsUser.addAttribute(new Attribute("Staff Type", "FA"));
        mrsUser.addAttribute(new Attribute("Phone Number", "012345"));
        mrsUser.addAttribute(new Attribute("Email", emailId));

        openMrsUserAdaptor.saveUser(mrsUser);

        org.openmrs.User mockOpenMRSUser = mock(org.openmrs.User.class);
        when(mockUserService.getUserByUsername(emailId)).thenReturn(mockOpenMRSUser);

        String newPassword = openMrsUserAdaptor.setNewPasswordForUser(emailId);
        verify(mockUserService).changePassword(mockOpenMRSUser, newPassword);

        openMrsUserAdaptor.changeCurrentUserPassword(newPassword, "p2");
        verify(mockUserService).changePassword(newPassword, "p2");

    }

    @Test
    public void shouldReturnAllUsers() {
        String id = "1";
        String systemId = "1234";
        String firstName = "test";
        String middleName = "test";
        String lastName = "test";
        String email = "test@gmail.com";
        String staffType = "admin";
        String phoneNumber = "0987654321";

        PersonAttributeType emailAttribute = mock(PersonAttributeType.class);
        PersonAttributeType phoneAttribute = mock(PersonAttributeType.class);
        PersonAttributeType staffTypeAttribute = mock(PersonAttributeType.class);

        when(emailAttribute.getName()).thenReturn("Email");
        when(phoneAttribute.getName()).thenReturn("Phone Number");
        when(staffTypeAttribute.getName()).thenReturn("Staff Type");

        when(mockPersonService.getPersonAttributeTypeByName("Email")).thenReturn(emailAttribute);
        when(mockPersonService.getPersonAttributeTypeByName("Phone Number")).thenReturn(phoneAttribute);
        when(mockPersonService.getPersonAttributeTypeByName("Staff Type")).thenReturn(staffTypeAttribute);

        Person person = new Person();

        person.addName(new PersonName(firstName, middleName, lastName));
        person.addAttribute(new PersonAttribute(emailAttribute, email));
        person.addAttribute(new PersonAttribute(phoneAttribute, phoneNumber));
        person.addAttribute(new PersonAttribute(staffTypeAttribute, staffType));

        org.openmrs.User openMRSUser = new org.openmrs.User(Integer.valueOf(id));
        person.setPersonId(1);
        openMRSUser.setPerson(person);
        openMRSUser.setSystemId(systemId);
        openMRSUser.setId(Integer.parseInt(id));

        List<org.openmrs.User> openMrsUsers = Arrays.asList(openMRSUser);
        when(mockUserService.getAllUsers()).thenReturn(openMrsUsers);
        List<MRSUser> returnedMRSUsers = openMrsUserAdaptor.getAllUsers();

        MRSUser actual = returnedMRSUsers.get(0);
        MRSUser expected = createAUser(id, systemId, firstName, middleName, lastName, email, staffType, phoneNumber);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSystemId(), actual.getSystemId());
        assertEquals(expected.getAttributes().size(), actual.getAttributes().size());
        assertEquals(expected.getAttributes().get(0).value(), actual.getAttributes().get(0).value());
        assertEquals(expected.getAttributes().get(0).name(), actual.getAttributes().get(0).name());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getMiddleName(), actual.getMiddleName());
        assertEquals(expected.getLastName(), actual.getLastName());
    }


    @Test
    public void shouldGetUserById() {
        org.openmrs.User mockOpenMrsUser = mock(org.openmrs.User.class);
        String userId = "1234567";
        MRSUser mrsUser = mock(MRSUser.class);
        OpenMRSUserAdaptor openMRSUserAdaptorSpy = spy(openMrsUserAdaptor);

        doReturn(mrsUser).when(openMRSUserAdaptorSpy).openMrsToMrsUser(mockOpenMrsUser);
        doReturn(mockOpenMrsUser).when(openMRSUserAdaptorSpy).getOpenMrsUserById(userId);

        assertThat(openMRSUserAdaptorSpy.getUserBySystemId(userId), is(mrsUser));

        doReturn(null).when(openMRSUserAdaptorSpy).getOpenMrsUserById(userId);
        assertThat(openMRSUserAdaptorSpy.getUserBySystemId(userId), is(equalTo(null)));

    }

    @Test
    public void shouldGetOpenMrsUserById() {
        org.openmrs.User mockOpenMrsUser = mock(org.openmrs.User.class);
        String userId = "1234567";

        when(mockUserService.getUserByUsername(userId)).thenReturn(mockOpenMrsUser);
        assertThat(openMrsUserAdaptor.getOpenMrsUserById(userId), is(mockOpenMrsUser));

        when(mockUserService.getUserByUsername(userId)).thenReturn(null);
        assertThat(openMrsUserAdaptor.getUserBySystemId(userId), is(equalTo(null)));
    }

    @Test
    public void shouldGetUserBySystemId() {
        org.openmrs.User mockOpenMrsUser = mock(org.openmrs.User.class);
        String userId = "1234567";
        MRSUser mrsUser = mock(MRSUser.class);
        OpenMRSUserAdaptor openMRSUserAdaptorSpy = spy(openMrsUserAdaptor);

        when(mockUserService.getUserByUsername(userId)).thenReturn(mockOpenMrsUser);
        doReturn(mrsUser).when(openMRSUserAdaptorSpy).openMrsToMrsUser(mockOpenMrsUser);

        assertThat(openMRSUserAdaptorSpy.getUserBySystemId(userId), is(mrsUser));

        when(mockUserService.getUserByUsername(userId)).thenReturn(null);
        assertThat(openMRSUserAdaptorSpy.getUserBySystemId(userId), is(equalTo(null)));

    }

    private MRSUser createAUser(String id, String systemId, String firstName, String middleName, String lastName, String email, String staffType, String phoneNumber) {
        MRSUser mrsUser = new MRSUser();
        mrsUser.id(id)
                .systemId(systemId)
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName);
        mrsUser.addAttribute(new Attribute("Phone Number", phoneNumber));
        mrsUser.addAttribute(new Attribute("Staff Type", staffType));
        mrsUser.addAttribute(new Attribute("Email", email));
        return mrsUser;
    }

}
