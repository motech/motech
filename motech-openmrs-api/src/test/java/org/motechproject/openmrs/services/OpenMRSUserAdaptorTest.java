package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.openmrs.model.Constants;
import org.motechproject.openmrs.model.Password;
import org.openmrs.Person;
import org.openmrs.PersonAttributeType;
import org.openmrs.Role;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSUserAdaptorTest {
    @Mock
    private UserService userService;
    @Mock
    private PersonService personService;

    private OpenMRSUserAdaptor adaptor;

    @Before
    public void setUp() {
        initMocks(this);
        adaptor = new OpenMRSUserAdaptor(userService, personService);
    }

    @Test
    public void testChangeCurrentUserPassword() throws Exception {
        adaptor.changeCurrentUserPassword("p1", "p2");
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

        HashMap userMap = adaptor.saveUser(mrsUser);

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

        adaptor.saveUser(mrsUser);

        org.openmrs.User mockOpenMRSUser = mock(org.openmrs.User.class);
        when(userService.getUserByUsername(emailId)).thenReturn(mockOpenMRSUser);

        String newPassword = adaptor.setNewPasswordForUser(emailId);
        verify(userService).changePassword(mockOpenMRSUser, newPassword);

        adaptor.changeCurrentUserPassword(newPassword, "p2");
        verify(userService).changePassword(newPassword, "p2");

    }


}
