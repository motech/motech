package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.openmrs.builder.UserBuilder;
import org.openmrs.*;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.db.DAOException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSUserAdaptorTest {
    @Mock
    private UserService mockUserService;
    @Mock
    private PersonService mockPersonService;

    private OpenMRSUserAdaptor openMrsUserAdaptor;

    public static final String PERSON_ATTRIBUTE_TYPE_EMAIL = "Email";
    public static final String PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER = "Phone Number";
    public static final String PERSON_ATTRIBUTE_TYPE_STAFF_TYPE = "Staff Type";

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

    @Test(expected = UserAlreadyExistsException.class)
    public void shouldThrowUserAlreadyExistsExceptionIfUserAlreadyExist() throws UserAlreadyExistsException {
        String userName = "userName";
        List<Attribute> attributes = new ArrayList<Attribute>();
        String email = "test@gmail.com";
        attributes.add(new Attribute("Email", email));
        MRSUser mrsUser = new MRSUser().userName(userName);
        mrsUser.person(new MRSPerson().attributes(attributes));
        User mockUser = mock(User.class);
        Person person = new Person();
        person.addName(new PersonName("given", "middle", "family"));

        when(mockUser.getSystemId()).thenReturn("1");
        when(mockUser.getPerson()).thenReturn(person);
        when(mockUserService.getUserByUsername(email)).thenReturn(mockUser);
        openMrsUserAdaptor.saveUser(mrsUser);
    }

    @Test
    public void shouldSaveANewUser() throws UserAlreadyExistsException {
        OpenMRSUserAdaptor adaptorSpy = spy(openMrsUserAdaptor);

        MRSUser mrsUser = mock(MRSUser.class);
        User openMRSUser = mock(User.class);

        MRSUser savedMRSUser = mock(MRSUser.class);
        User savedOpenMRSUser = mock(User.class);
        MRSPerson mrsPerson = mock(MRSPerson.class);

        doReturn(openMRSUser).when(adaptorSpy).mrsUserToOpenMRSUser(mrsUser);
        doReturn(savedMRSUser).when(adaptorSpy).openMrsToMrsUser(savedOpenMRSUser);
        when(mrsUser.getPerson()).thenReturn(mrsPerson);
        when(savedOpenMRSUser.getSystemId()).thenReturn("aaaaa");
        when(mockUserService.saveUser(eq(openMRSUser), Matchers.<String>any())).thenReturn(savedOpenMRSUser);

        Map<String, Object> map = adaptorSpy.saveUser(mrsUser);
        assertThat((MRSUser) map.get(OpenMRSUserAdaptor.USER_KEY), is(equalTo(savedMRSUser)));
    }

    private void mockPersonServiceForAttributes(PersonAttributeType phoneAttribute, PersonAttributeType staffTypeAttribute, PersonAttributeType emailAttribute) {
        when(mockPersonService.getPersonAttributeTypeByName(PERSON_ATTRIBUTE_TYPE_STAFF_TYPE)).thenReturn(staffTypeAttribute);
        when(mockPersonService.getPersonAttributeTypeByName(PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER)).thenReturn(phoneAttribute);
        when(mockPersonService.getPersonAttributeTypeByName(PERSON_ATTRIBUTE_TYPE_EMAIL)).thenReturn(emailAttribute);
    }

    @Test
    @Ignore
    public void shouldResetPasswordGivenUserEmailId() throws UsernameNotFoundException, UserAlreadyExistsException {
        MRSUser mrsUser = new MRSUser();
        Role role = new Role();
        final String emailId = "jack@daniels.com";

        when(mockUserService.getRole(mrsUser.getSecurityRole())).thenReturn(role);

        MRSPerson mrsPerson = new MRSPerson().firstName("Jack").middleName("H").lastName("Daniels").addAttribute(new Attribute("Staff Type", "FA"))
                .addAttribute(new Attribute("Phone Number", "012345"))
                .addAttribute(new Attribute("Email", emailId));
        mrsUser.securityRole("provider").systemId("nonadmin");
        mrsUser.person(mrsPerson);

        PersonAttributeType phoneNumberAttributeType = personAttributeType(PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER);
        PersonAttributeType staffTypeAttributeType = personAttributeType(PERSON_ATTRIBUTE_TYPE_STAFF_TYPE);
        PersonAttributeType emailAttributeType = personAttributeType(PERSON_ATTRIBUTE_TYPE_EMAIL);

        mockPersonServiceForAttributes(phoneNumberAttributeType, staffTypeAttributeType, emailAttributeType);

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

        mockPersonServiceForAttributes(phoneAttribute, staffTypeAttribute, emailAttribute);

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
        MRSPerson actualMRSPerson = actual.getPerson();
        MRSUser expected = createAUser(id, systemId, firstName, middleName, lastName, email, staffType, phoneNumber);
        MRSPerson expectedMRSPerson = expected.getPerson();
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSystemId(), actual.getSystemId());
        assertEquals(expectedMRSPerson.getAttributes().size(), actualMRSPerson.getAttributes().size());
        assertEquals(expectedMRSPerson.getAttributes().get(0).value(), actualMRSPerson.getAttributes().get(0).value());
        assertEquals(expectedMRSPerson.getAttributes().get(0).name(), actualMRSPerson.getAttributes().get(0).name());
        assertEquals(expectedMRSPerson.getFirstName(), actualMRSPerson.getFirstName());
        assertEquals(expectedMRSPerson.getMiddleName(), actualMRSPerson.getMiddleName());
        assertEquals(expectedMRSPerson.getLastName(), actualMRSPerson.getLastName());
    }


    @Test
    public void shouldGetUserById() {
        org.openmrs.User mockOpenMrsUser = mock(org.openmrs.User.class);
        String userId = "1234567";
        MRSUser mrsUser = mock(MRSUser.class);
        OpenMRSUserAdaptor openMRSUserAdaptorSpy = spy(openMrsUserAdaptor);

        doReturn(mrsUser).when(openMRSUserAdaptorSpy).openMrsToMrsUser(mockOpenMrsUser);
        doReturn(mockOpenMrsUser).when(openMRSUserAdaptorSpy).getOpenMrsUserByUserName(userId);

        assertThat(openMRSUserAdaptorSpy.getUserByUserName(userId), is(mrsUser));

        doReturn(null).when(openMRSUserAdaptorSpy).getOpenMrsUserByUserName(userId);
        assertThat(openMRSUserAdaptorSpy.getUserByUserName(userId), is(equalTo(null)));

    }

    @Test
    public void shouldGetOpenMrsUserById() {
        org.openmrs.User mockOpenMrsUser = mock(org.openmrs.User.class);
        String userId = "1234567";

        when(mockUserService.getUserByUsername(userId)).thenReturn(mockOpenMrsUser);
        assertThat(openMrsUserAdaptor.getOpenMrsUserByUserName(userId), is(mockOpenMrsUser));

        when(mockUserService.getUserByUsername(userId)).thenReturn(null);
        assertThat(openMrsUserAdaptor.getUserByUserName(userId), is(equalTo(null)));
    }

    @Test
    public void shouldGetUserBySystemId() {
        org.openmrs.User mockOpenMrsUser = mock(org.openmrs.User.class);
        String userId = "1234567";
        MRSUser mrsUser = mock(MRSUser.class);
        OpenMRSUserAdaptor openMRSUserAdaptorSpy = spy(openMrsUserAdaptor);

        when(mockUserService.getUserByUsername(userId)).thenReturn(mockOpenMrsUser);
        doReturn(mrsUser).when(openMRSUserAdaptorSpy).openMrsToMrsUser(mockOpenMrsUser);

        assertThat(openMRSUserAdaptorSpy.getUserByUserName(userId), is(mrsUser));

        when(mockUserService.getUserByUsername(userId)).thenReturn(null);
        assertThat(openMRSUserAdaptorSpy.getUserByUserName(userId), is(equalTo(null)));

    }

    private MRSUser createAUser(String id, String systemId, String firstName, String middleName, String lastName, String email, String staffType, String phoneNumber) {
        MRSUser mrsUser = new MRSUser();
        MRSPerson mrsPerson = new MRSPerson().firstName(firstName)
                .middleName(middleName)
                .lastName(lastName).addAttribute(new Attribute("Phone Number", phoneNumber))
                .addAttribute(new Attribute("Staff Type", staffType))
                .addAttribute(new Attribute("Email", email));

        mrsUser.id(id).person(mrsPerson)
                .systemId(systemId);

        return mrsUser;
    }


    private void assertMRSUser(MRSUser actual, MRSUser expected) {
        MRSPerson expectedPerson = expected.getPerson();
        MRSPerson actualPerson = actual.getPerson();
        assertEquals(expected.getUserName(), actual.getUserName());
        assertEquals(expectedPerson.getFirstName(), actualPerson.getFirstName());
        assertEquals(expectedPerson.getMiddleName(), actualPerson.getMiddleName());
        assertEquals(expectedPerson.getLastName(), actualPerson.getLastName());
        assertEquals(expected.getSecurityRole(), actual.getSecurityRole());
        assertEquals(expectedPerson.attrValue(PERSON_ATTRIBUTE_TYPE_STAFF_TYPE), actualPerson.attrValue(PERSON_ATTRIBUTE_TYPE_STAFF_TYPE));
        assertEquals(expectedPerson.attrValue(PERSON_ATTRIBUTE_TYPE_EMAIL), actualPerson.attrValue(PERSON_ATTRIBUTE_TYPE_EMAIL));
        assertEquals(expectedPerson.attrValue(PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER), actualPerson.attrValue(PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER));
    }

    @Test
    public void shouldConvertOpenMRSUserToMRSUser() {
        Person person = new Person();
        person.addName(new PersonName("givenName", "middleName", "familyName"));
        person.setAttributes(personAttributes("staffType", "10101010", "aa@uu.com"));
        User openMRSUser = new UserBuilder().userId(12).person(person).roles(new HashSet<Role>(asList(new Role("provider")))).systemId("123").username("userName")
                .build();
        MRSUser mrsUser = openMrsUserAdaptor.openMrsToMrsUser(openMRSUser);
        assertUserAndMRSUser(openMRSUser, mrsUser);
    }

    @Test
    public void shouldConvertMRSUserToOpenMRSUser() {
        String securityRole = "role";

        PersonAttributeType phoneNumberAttributeType = personAttributeType(PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER);
        PersonAttributeType staffTypeAttributeType = personAttributeType(PERSON_ATTRIBUTE_TYPE_STAFF_TYPE);
        PersonAttributeType emailAttributeType = personAttributeType(PERSON_ATTRIBUTE_TYPE_EMAIL);

        MRSPerson mrsPerson = new MRSPerson().firstName("firstName").middleName("middleName").
                lastName("lastName").addAttribute(new Attribute(PERSON_ATTRIBUTE_TYPE_EMAIL, "email")).
                addAttribute(new Attribute(PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, "12345")).addAttribute(new Attribute(PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, "provider"));

        MRSUser mrsUser = new MRSUser().securityRole(securityRole).userName("userName").person(mrsPerson).systemId("123");

        mockPersonServiceForAttributes(phoneNumberAttributeType, staffTypeAttributeType, emailAttributeType);

        when(mockUserService.getRole("role")).thenReturn(new Role("role"));
        User openMRSUser = openMrsUserAdaptor.mrsUserToOpenMRSUser(mrsUser);
        assertUserAndMRSUser(openMRSUser, mrsUser);
    }

    private void assertUserAndMRSUser(User openMRSUser, MRSUser mrsUser) {
        MRSPerson mrsPerson = mrsUser.getPerson();
        assertEquals(openMRSUser.getUsername(), mrsUser.getUserName());
        assertEquals(openMRSUser.getGivenName(), mrsPerson.getFirstName());
        assertEquals(openMRSUser.getPersonName().getMiddleName(), mrsPerson.getMiddleName());
        assertEquals(openMRSUser.getPersonName().getFamilyName(), mrsPerson.getLastName());
        assertEquals(((Role) openMRSUser.getRoles().toArray()[0]).getRole(), mrsUser.getSecurityRole());
        assertEquals(getOpenMRSPersonAttributeValue(openMRSUser, PERSON_ATTRIBUTE_TYPE_STAFF_TYPE), mrsPerson.attrValue(PERSON_ATTRIBUTE_TYPE_STAFF_TYPE));
        assertEquals(getOpenMRSPersonAttributeValue(openMRSUser, PERSON_ATTRIBUTE_TYPE_EMAIL), mrsPerson.attrValue(PERSON_ATTRIBUTE_TYPE_EMAIL));
        assertEquals(getOpenMRSPersonAttributeValue(openMRSUser, PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER), mrsPerson.attrValue(PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER));
    }

    private String getOpenMRSPersonAttributeValue(User openMRSUser, String name) {
        return openMRSUser.getPerson().getAttribute(name).getValue();
    }

    private Set<PersonAttribute> personAttributes(String staffType, String phoneNo, String email) {
        return new HashSet<PersonAttribute>(asList(new PersonAttribute(personAttributeType(PERSON_ATTRIBUTE_TYPE_STAFF_TYPE), staffType),
                new PersonAttribute(personAttributeType(PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER), phoneNo),
                new PersonAttribute(personAttributeType(PERSON_ATTRIBUTE_TYPE_EMAIL), email)));
    }

    private PersonAttributeType personAttributeType(String name) {
        PersonAttributeType attr = new PersonAttributeType((int) (Math.random() * 10000));
        attr.setName(name);
        return attr;
    }

}
