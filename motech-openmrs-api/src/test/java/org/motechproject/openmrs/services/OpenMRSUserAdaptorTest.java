package org.motechproject.openmrs.services;

import ch.lambdaj.function.argument.Argument;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.model.UserAttribute;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.openmrs.model.Constants;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Role;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.db.DAOException;
import org.openmrs.util.OpenmrsConstants;

import static junit.framework.Assert.assertEquals;
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

        when(userService.getRole(mrsUser.securityRole())).thenReturn(role);
        PersonAttributeType phoneAttribute = mock(PersonAttributeType.class);
        PersonAttributeType staffTypeAttribute = mock(PersonAttributeType.class);
        PersonAttributeType emailAttribute = mock(PersonAttributeType.class);

        when(personService.getPersonAttributeTypeByName("Staff Type")).thenReturn(staffTypeAttribute);
        when(personService.getPersonAttributeTypeByName("Phone Number")).thenReturn(phoneAttribute);
        when(personService.getPersonAttributeTypeByName("Email")).thenReturn(emailAttribute);

        mrsUser.firstName("Jack").middleName("H").lastName("Daniels").securityRole("provider");
        mrsUser.addAttribute(new UserAttribute("Staff Type", "FA"));
        mrsUser.addAttribute(new UserAttribute("Phone Number", "012345"));
        mrsUser.addAttribute(new UserAttribute("Email", "jack@daniels.com"));

        adaptor.saveUser(mrsUser);

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
    }
}
