package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.internal.matchers.Null;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Facility;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.openmrs.model.Constants;
import org.openmrs.*;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.db.DAOException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSUserAdaptorTest {
    @Mock
    private UserService userService;
    @Mock
    private PersonService personService;

    private OpenMRSUserAdaptor openMrsUserAdaptor;

    @Before
    public void setUp() {
        initMocks(this);
        openMrsUserAdaptor = new OpenMRSUserAdaptor(userService, personService);
    }

    @Test
    public void testChangeCurrentUserPassword() throws Exception {
        openMrsUserAdaptor.changeCurrentUserPassword("p1", "p2");
        verify(userService).changePassword("p1", "p2");
    }

    @Test(expected = MRSException.class)
    public void testChangeCurrentUserPasswordFailed() throws Exception {
        doThrow(mock(DAOException.class)).when(userService).changePassword("p1", "p2");
        new OpenMRSUserAdaptor(userService, personService).changeCurrentUserPassword("p1", "p2");
    }

    @Test
    public void shouldSaveANewUser() throws UserAlreadyExistsException {
        User mrsUser = new User();
        Role role = new Role();

        when(userService.getRole(mrsUser.getSecurityRole())).thenReturn(role);
        PersonAttributeType phoneAttribute = mock(PersonAttributeType.class);
        PersonAttributeType staffTypeAttribute = mock(PersonAttributeType.class);
        PersonAttributeType emailAttribute = mock(PersonAttributeType.class);

        when(personService.getPersonAttributeTypeByName("Staff Type")).thenReturn(staffTypeAttribute);
        when(personService.getPersonAttributeTypeByName("Phone Number")).thenReturn(phoneAttribute);
        when(personService.getPersonAttributeTypeByName("Email")).thenReturn(emailAttribute);

        mrsUser.firstName("Jack").middleName("H").lastName("Daniels").securityRole("provider");
        mrsUser.addAttribute(new Attribute("Staff Type", "FA"));
        mrsUser.addAttribute(new Attribute("Phone Number", "012345"));
        mrsUser.addAttribute(new Attribute("Email", "jack@daniels.com"));

        HashMap userMap = openMrsUserAdaptor.saveUser(mrsUser);

        ArgumentCaptor<org.openmrs.User> captor = ArgumentCaptor.forClass(org.openmrs.User.class);
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(userService).saveUser(captor.capture(), passwordCaptor.capture());
        verify(userService).generateSystemId();
        String capturedPassword = passwordCaptor.getValue();
        assertEquals(8, capturedPassword.length());

        org.openmrs.User capturedUser = captor.getValue();
        Person person = capturedUser.getPerson();
        assertEquals("Jack", person.getGivenName());
        assertEquals("H", person.getMiddleName());
        assertEquals("Daniels", person.getFamilyName());
        assertEquals("FA", person.getAttribute(staffTypeAttribute).getValue());
        assertEquals("012345", person.getAttribute(phoneAttribute).getValue());
        assertEquals("jack@daniels.com", person.getAttribute(emailAttribute).getValue());
        assertEquals("?", person.getGender());

//        assertEquals("jack@daniels.com",userMap.get("userLoginId"));
    }

    @Test
    @Ignore
    public void shouldResetPasswordGivenUserEmailId() throws UsernameNotFoundException, UserAlreadyExistsException {
        User mrsUser = new User();
        Role role = new Role();
        final String emailId = "jack@daniels.com";

        when(userService.getRole(mrsUser.getSecurityRole())).thenReturn(role);
        PersonAttributeType phoneAttribute = mock(PersonAttributeType.class);
        PersonAttributeType staffTypeAttribute = mock(PersonAttributeType.class);
        PersonAttributeType emailAttribute = mock(PersonAttributeType.class);

        when(personService.getPersonAttributeTypeByName("Staff Type")).thenReturn(staffTypeAttribute);
        when(personService.getPersonAttributeTypeByName("Phone Number")).thenReturn(phoneAttribute);
        when(personService.getPersonAttributeTypeByName("Email")).thenReturn(emailAttribute);

        mrsUser.firstName("Jack").middleName("H").lastName("Daniels").securityRole("provider");
        mrsUser.addAttribute(new Attribute("Staff Type", "FA"));
        mrsUser.addAttribute(new Attribute("Phone Number", "012345"));
        mrsUser.addAttribute(new Attribute("Email", emailId));

        openMrsUserAdaptor.saveUser(mrsUser);

        org.openmrs.User mockOpenMRSUser = mock(org.openmrs.User.class);
        when(userService.getUserByUsername(emailId)).thenReturn(mockOpenMRSUser);

        String newPassword = openMrsUserAdaptor.setNewPasswordForUser(emailId);
        verify(userService).changePassword(mockOpenMRSUser, newPassword);

        openMrsUserAdaptor.changeCurrentUserPassword(newPassword, "p2");
        verify(userService).changePassword(newPassword, "p2");

    }

    @Test
    public void shouldReturnAllUsers() {
        String id = "1";
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

        when(personService.getPersonAttributeTypeByName("Email")).thenReturn(emailAttribute);
        when(personService.getPersonAttributeTypeByName("Phone Number")).thenReturn(phoneAttribute);
        when(personService.getPersonAttributeTypeByName("Staff Type")).thenReturn(staffTypeAttribute);

        Person person = new Person();

        person.addName(new PersonName(firstName, middleName, lastName));
        person.addAttribute(new PersonAttribute(emailAttribute, email));
        person.addAttribute(new PersonAttribute(phoneAttribute,phoneNumber));
        person.addAttribute(new PersonAttribute(staffTypeAttribute,staffType));

        org.openmrs.User openMRSUser = new org.openmrs.User(Integer.valueOf(id));
        openMRSUser.setPerson(person);
        openMRSUser.setSystemId(id);

        List<org.openmrs.User> openMrsUsers = Arrays.asList(openMRSUser);
        when(userService.getAllUsers()).thenReturn(openMrsUsers);
        List<User> returnedUsers = openMrsUserAdaptor.getAllUsers();

        User actual = returnedUsers.get(0);
        User expected = createAUser(id, firstName, middleName, lastName, email, staffType, phoneNumber);
        assertEquals(expected.getId(),actual.getId());
        assertEquals(expected.getAttributes().size(), actual.getAttributes().size());
        assertEquals(expected.getAttributes().get(0).value(),actual.getAttributes().get(0).value());
        assertEquals(expected.getAttributes().get(0).name(),actual.getAttributes().get(0).name());
        assertEquals(expected.getFirstName(),actual.getFirstName());
        assertEquals(expected.getMiddleName(),actual.getMiddleName());
        assertEquals(expected.getLastName(),actual.getLastName());

    }

    private User createAUser(String id, String firstName, String middleName, String lastName, String email, String staffType, String phoneNumber) {
        User mrsUser = new User();
        mrsUser.id(id)
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName);
        mrsUser.addAttribute(new Attribute("Phone Number", phoneNumber));
        mrsUser.addAttribute(new Attribute("Staff Type", staffType));
        mrsUser.addAttribute(new Attribute("Email", email));
        return mrsUser;
    }


}
