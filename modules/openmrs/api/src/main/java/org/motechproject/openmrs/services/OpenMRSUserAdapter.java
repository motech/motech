package org.motechproject.openmrs.services;

import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.model.OpenMRSUser;
import org.motechproject.mrs.model.Password;
import org.motechproject.mrs.services.UserAdapter;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Service
public class OpenMRSUserAdapter implements UserAdapter {
    private static final Integer PASSWORD_LENGTH = 8;
    private static final String PERSON_UNKNOWN_GENDER = "?";

    private UserService userService;
    private PersonService personService;
    private OpenMRSPersonAdapter openMRSPersonAdapter;

    @Autowired
    public OpenMRSUserAdapter(UserService userService, PersonService personService, OpenMRSPersonAdapter openMRSPersonAdapter) {
        this.userService = userService;
        this.personService = personService;
        this.openMRSPersonAdapter = openMRSPersonAdapter;
    }

    /**
     * Changes the password of the user.
     *
     * @param currentPassword Old password
     * @param newPassword     New password
     * @throws MRSException Thrown when change password fails
     */
    @Override
    public void changeCurrentUserPassword(String currentPassword, String newPassword) {
        try {
            userService.changePassword(currentPassword, newPassword);
        } catch (APIException e) {
            throw new MRSException(e);
        }
    }

    /**
     * Creates a new MRSUser
     *
     * @param mrsUser Instance of the User object to be saved
     * @return A Map containing saved user's data
     * @throws UserAlreadyExistsException Thrown if the user already exists
     */
    @Override
    public Map<String, Object> saveUser(org.motechproject.mrs.domain.User mrsUser) throws UserAlreadyExistsException {
        OpenMRSPerson person = (OpenMRSPerson) mrsUser.getPerson();
        OpenMRSUser userByUserName = getUserByUserName(person.attrValue("Email"));
        if (userByUserName != null && !isSystemAdmin(userByUserName.getSystemId())) {
            throw new UserAlreadyExistsException();
        }
        return save(mrsUser);
    }

    /**
     * Updates User attributes if found, else creates the user.
     *
     * @param mrsUser MRS User object
     * @return A Map containing saved user's data
     */
    @Override
    public Map<String, Object> updateUser(org.motechproject.mrs.domain.User mrsUser) {
        return save(mrsUser);
    }

    /**
     * Finds user by UserName
     *
     * @param userId User's unique Identifier ( email or MOTECH id )
     * @return The User object, if found, else null.
     */
    @Override
    public OpenMRSUser getUserByUserName(String userId) {
        org.openmrs.User openMrsUser = getOpenMrsUserByUserName(userId);
        if (openMrsUser == null) {
            return null;
        }
        return (!isSystemAdmin(openMrsUser.getSystemId())) ? openMrsToMrsUser(openMrsUser)
                : new OpenMRSUser().systemId(openMrsUser.getSystemId()).id(Integer.toString(openMrsUser.getId()))
                .person(new OpenMRSPerson().id(Integer.toString(openMrsUser.getPerson().getId())));
    }

    org.openmrs.User getOpenMrsUserByUserName(String userName) {
        return userService.getUserByUsername(userName);
    }

    org.openmrs.User getOpenMrsUserById(String id) {
        return userService.getUser(Integer.valueOf(id));
    }

    /**
     * Gets all users present in the openMRS system
     *
     * @return List of all Users if users exist, else empty list
     */
    @Override
    public List<org.motechproject.mrs.domain.User> getAllUsers() {
        List<org.motechproject.mrs.domain.User> mrsUsers = new ArrayList<org.motechproject.mrs.domain.User>();
        List<org.openmrs.User> openMRSUsers = userService.getAllUsers();
        for (org.openmrs.User openMRSUser : openMRSUsers) {
            if (isSystemAdmin(openMRSUser.getSystemId())) {
                continue;
            }
            mrsUsers.add(openMrsToMrsUser(openMRSUser));
        }
        return mrsUsers;
    }

    OpenMRSUser openMrsToMrsUser(org.openmrs.User openMRSUser) {
        OpenMRSUser mrsUser = new OpenMRSUser();
        OpenMRSPerson mrsPerson = openMRSPersonAdapter.openMRSToMRSPerson(openMRSUser.getPerson());

        mrsUser.id(Integer.toString(openMRSUser.getId())).systemId(openMRSUser.getSystemId()).userName(openMRSUser.getUsername()).person(mrsPerson).
                securityRole(getRoleFromOpenMRSUser(openMRSUser.getRoles()));

        return mrsUser;
    }

    private boolean isSystemAdmin(String systemId) {
        return "admin".equals(systemId) || "daemon".equals(systemId);
    }

    public String getRoleFromOpenMRSUser(Set<Role> roles) {
        return roles != null && !roles.isEmpty() ? roles.iterator().next().getRole() : null;
    }

    private Map<String, Object> save(org.motechproject.mrs.domain.User mrsUser) {

        org.openmrs.User openMRSUser = mrsUserToOpenMRSUser(mrsUser);
        final String password = new Password(PASSWORD_LENGTH).create();
        Map<String, Object> userMap = new HashMap<String, Object>();
        final org.openmrs.User savedUser = userService.saveUser(openMRSUser, password);

        userMap.put(USER_KEY, openMrsToMrsUser(savedUser));
        userMap.put(PASSWORD_KEY, password);
        return userMap;
    }

    org.openmrs.User mrsUserToOpenMRSUser(org.motechproject.mrs.domain.User mrsUser) {
        User user = getOrCreateUser(mrsUser.getUserId());
        Person person = user.getPerson();
        clearAttributes(user);

        OpenMRSPerson mrsPerson = (OpenMRSPerson) mrsUser.getPerson();
        PersonName personName = new PersonName(mrsPerson.getFirstName(), mrsPerson.getMiddleName(), mrsPerson.getLastName());
        person.addName(personName);
        person.setGender(PERSON_UNKNOWN_GENDER);

        for (Attribute attribute : mrsPerson.getAttributes()) {
            PersonAttributeType attributeType = personService.getPersonAttributeTypeByName(attribute.getName());
            person.addAttribute(new PersonAttribute(attributeType, attribute.getValue()));
        }

        Role role = userService.getRole(mrsUser.getSecurityRole());
        user.addRole(role);
        addProviderRole(user, role);
        user.setSystemId(mrsUser.getSystemId());
        user.setUsername(mrsUser.getUserName());
        return user;
    }

    private void addProviderRole(User user, Role role) {
        if (!role.equals(OpenmrsConstants.PROVIDER_ROLE)) {
            user.addRole(userService.getRole(OpenmrsConstants.PROVIDER_ROLE));
        }
    }

    private void clearAttributes(User user) {
        Person person = user.getPerson();
        if (person.getNames() != null) {
            person.getNames().clear();
        }
        if (person.getAttributes() != null) {
            person.getAttributes().clear();
        }
        if (user.getRoles() != null) {
            user.getRoles().clear();
        }
    }

    private org.openmrs.User getOrCreateUser(String dbId) {
        return isNotEmpty(dbId) ? userService.getUser(Integer.parseInt(dbId)) : new org.openmrs.User(new Person());
    }

    /**
     * Resets the password of a given User
     *
     * @param userId User's unique identifier (email or MOTECH id)
     * @return New password
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public String setNewPasswordForUser(String userId) {
        org.openmrs.User userByUsername;
        try {
            userByUsername = userService.getUserByUsername(userId);
        } catch (APIException e) {
            throw new UsernameNotFoundException("User was not found", e);
        }

        String newPassword = new Password(PASSWORD_LENGTH).create();
        Context.getUserService().changePassword(userByUsername, newPassword);
        return newPassword;
    }
}

