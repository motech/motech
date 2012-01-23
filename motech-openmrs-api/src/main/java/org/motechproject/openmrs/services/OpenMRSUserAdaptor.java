package org.motechproject.openmrs.services;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.mrs.services.MRSUserAdaptor;
import org.motechproject.openmrs.model.Password;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class OpenMRSUserAdaptor implements MRSUserAdaptor {
    private static Integer PASSWORD_LENGTH = 8;
    private static String PERSON_UNKNOWN_GENDER = "?";

    private UserService userService;
    private PersonService personService;
    public static final String USER_KEY = "mrsUser";
    public static final String PASSWORD_USER_KEY = "password";
    private OpenMRSPersonAdaptor openMRSPersonAdaptor;

    @Autowired
    public OpenMRSUserAdaptor(UserService userService, PersonService personService, OpenMRSPersonAdaptor openMRSPersonAdaptor) {
        this.userService = userService;
        this.personService = personService;
        this.openMRSPersonAdaptor = openMRSPersonAdaptor;
    }

    @Override
    public void changeCurrentUserPassword(String currentPassword, String newPassword) {
        try {
            userService.changePassword(currentPassword, newPassword);
        } catch (APIException e) {
            throw new MRSException(e);
        }
    }

    @Override
    public Map saveUser(MRSUser mrsUser) throws UserAlreadyExistsException {
        if (getUserByUserName(mrsUser.getPerson().attrValue("Email")) != null) {
            throw new UserAlreadyExistsException();
        }
        return save(mrsUser);
    }

    @Override
    public Map<String, Object> updateUser(MRSUser mrsUser) {
        return save(mrsUser);
    }

    @Override
    public MRSUser getUserByUserName(String userName) {
        org.openmrs.User openMrsUser = getOpenMrsUserByUserName(userName);
        return (openMrsUser != null) ? openMrsToMrsUser(openMrsUser) : null;
    }

    public org.openmrs.User getOpenMrsUserByUserName(String userName) {
        return userService.getUserByUsername(userName);
    }

    public org.openmrs.User getOpenMrsUserById(String id) {
        return userService.getUser(Integer.valueOf(id));
    }


    @Override
    public List<MRSUser> getAllUsers() {
        List<MRSUser> mrsUsers = new ArrayList<MRSUser>();
        List<org.openmrs.User> openMRSUsers = userService.getAllUsers();
        for (org.openmrs.User openMRSUser : openMRSUsers) {
            MRSUser mrsUser = openMrsToMrsUser(openMRSUser);
            if (mrsUser == null) continue;
            mrsUsers.add(mrsUser);
        }
        return mrsUsers;
    }

    MRSUser openMrsToMrsUser(org.openmrs.User openMRSUser) {
        MRSUser mrsUser = new MRSUser();
        MRSPerson mrsPerson =   openMRSPersonAdaptor.openMRSToMRSPerson(openMRSUser.getPerson());

        mrsUser.id(Integer.toString(openMRSUser.getId())).systemId(openMRSUser.getSystemId()).userName(openMRSUser.getUsername()).person(mrsPerson).
                securityRole(getRoleFromOpenMRSUser(openMRSUser.getRoles()));

        return mrsUser;
    }

    public String getRoleFromOpenMRSUser(Set<Role> roles) {
        return roles != null && !roles.isEmpty() ? roles.iterator().next().getRole() : null;
    }

    private Map<String, Object> save(MRSUser mrsUser) {

        org.openmrs.User openMRSUser = mrsUserToOpenMRSUser(mrsUser);
        final String password = new Password(PASSWORD_LENGTH).create();
        Map<String, Object> userMap = new HashMap<String, Object>();
        final org.openmrs.User savedUser = userService.saveUser(openMRSUser, password);

        userMap.put(USER_KEY, openMrsToMrsUser(savedUser));
        userMap.put(PASSWORD_USER_KEY, password);
        return userMap;
    }

    org.openmrs.User mrsUserToOpenMRSUser(MRSUser mrsUser) {
        User user = getOrCreateUser(mrsUser.getId());
        Person person = user.getPerson();
        clearAttributes(user);

        MRSPerson mrsPerson = mrsUser.getPerson();
        PersonName personName = new PersonName(mrsPerson.getFirstName(), mrsPerson.getMiddleName(), mrsPerson.getLastName());
        person.addName(personName);
        person.setGender(PERSON_UNKNOWN_GENDER);

        for (Attribute attribute : mrsPerson.getAttributes()) {
            PersonAttributeType attributeType = personService.getPersonAttributeTypeByName(attribute.name());
            person.addAttribute(new PersonAttribute(attributeType, attribute.value()));
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
        if (person.getNames() != null) person.getNames().clear();
        if (person.getAttributes() != null) person.getAttributes().clear();
        if (user.getRoles() != null) user.getRoles().clear();
    }

    private org.openmrs.User getOrCreateUser(String dbId) {
        return isNotEmpty(dbId) ? userService.getUser(Integer.parseInt(dbId)) : new org.openmrs.User(new Person());
    }

    @Override
    public String setNewPasswordForUser(String emailID) throws UsernameNotFoundException {
        org.openmrs.User userByUsername;
        try {
            userByUsername = userService.getUserByUsername(emailID);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User was not found");
        }

        String newPassword = new Password(PASSWORD_LENGTH).create();
        Context.getUserService().changePassword(userByUsername, newPassword);
        return newPassword;
    }
}

