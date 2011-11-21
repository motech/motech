package org.motechproject.openmrs.services;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.mrs.services.MRSUserAdaptor;
import org.motechproject.openmrs.model.Constants;
import org.motechproject.openmrs.model.Password;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenMRSUserAdaptor implements MRSUserAdaptor {
    private UserService userService;
    private PersonService personService;

    @Autowired
    public OpenMRSUserAdaptor(UserService userService, PersonService personService) {
        this.userService = userService;
        this.personService = personService;
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
    public Map saveUser(User mrsUser) throws UserAlreadyExistsException {
        org.openmrs.User user = new org.openmrs.User();
        org.openmrs.Person person = new org.openmrs.Person();
        PersonName personName = new PersonName(mrsUser.getFirstName(), mrsUser.getMiddleName(), mrsUser.getLastName());
        person.addName(personName);
        person.setGender(Constants.PERSON_UNKNOWN_GENDER);
        final String password = new Password(Constants.PASSWORD_LENGTH).create();

        for (Attribute attribute : mrsUser.getAttributes()) {
            PersonAttributeType attributeType = personService.getPersonAttributeTypeByName(attribute.name());
            person.addAttribute(new PersonAttribute(attributeType, attribute.value()));
        }
        Role role = userService.getRole(mrsUser.getSecurityRole());
        user.addRole(role);

        String id = mrsUser.getId();

        if (userService.getUserByUsername(id) != null) throw new UserAlreadyExistsException();
        user.setSystemId(id);
        user.setPerson(person);

        Map userMap = new HashMap();
        userMap.put("openMRSUser", user);
        userMap.put("password", password);

        userService.saveUser(user, password);
        return userMap;
    }

    @Override
    public String setNewPasswordForUser(String emailID) throws UsernameNotFoundException {

        org.openmrs.User userByUsername;
        Context.openSession();
        Context.authenticate("admin", "P@ssw0rd");
        try {
            userByUsername = userService.getUserByUsername(emailID);
        } catch (Exception e) {
            Context.closeSession();
            throw new UsernameNotFoundException("User was not found");
        }

        String newPassword = new Password(Constants.PASSWORD_LENGTH).create();
        try {
            Context.getUserService().changePassword(userByUsername, newPassword);
        } finally {
            Context.closeSession();
        }
        return newPassword;
    }


    @Override
    public List<User> getAllUsers() {
        List<User> mrsUsers = new ArrayList<User>();
        List<org.openmrs.User> openMRSUsers = userService.getAllUsers();
        for (org.openmrs.User openMRSUser : openMRSUsers) {
            User mrsUser = new User();
            if (openMRSUser.getSystemId().equals("daemon") || openMRSUser.getSystemId().equals("admin"))
                continue;
            Person person = openMRSUser.getPerson();
            PersonName personName = person.getPersonName();

            mrsUser.id(Integer.toString(openMRSUser.getId())).firstName(personName.getGivenName()).middleName(personName.getMiddleName()).lastName(personName.getFamilyName());

            for (PersonAttribute personAttribute : person.getAttributes()) {
                mrsUser.addAttribute(new Attribute(personAttribute.getAttributeType().getName(), personAttribute.getValue()));
            }
            mrsUsers.add(mrsUser);
        }
        return mrsUsers;
    }
}

