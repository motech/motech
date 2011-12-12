package org.motechproject.openmrs.services;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.mrs.services.MRSUserAdaptor;
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

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class OpenMRSUserAdaptor implements MRSUserAdaptor {
    private static Integer PASSWORD_LENGTH = 8;
    private static String PERSON_UNKNOWN_GENDER = "?";

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
    public Map saveUser(MRSUser mrsUser) throws UserAlreadyExistsException {
        if (getUserByUserName(mrsUser.getUserName()) != null) {
            throw new UserAlreadyExistsException();
        }
        return save(mrsUser);
    }

    @Override
    public Map updateUser(MRSUser mrsUser) throws UserAlreadyExistsException {
        return save(mrsUser);
    }

    @Override
    public MRSUser getUserByUserName(String userName) {
        org.openmrs.User openMrsUser = getOpenMrsUserByUserName(userName);
        if (openMrsUser != null) {
            return openMrsToMrsUser(openMrsUser);
        }
        return null;
    }

    public org.openmrs.User getOpenMrsUserByUserName(String userName) {
        return userService.getUserByUsername(userName);
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

    protected MRSUser openMrsToMrsUser(org.openmrs.User openMRSUser) {
        MRSUser mrsUser = new MRSUser();
        if (openMRSUser.getSystemId().equals("admin") || openMRSUser.getSystemId().equals("daemon"))
            return null;
        Person person = openMRSUser.getPerson();
        PersonName personName = person.getPersonName();

        mrsUser.id(Integer.toString(openMRSUser.getId())).systemId(openMRSUser.getSystemId()).firstName(personName.getGivenName()).middleName(personName.getMiddleName())
                .lastName(personName.getFamilyName());

        for (PersonAttribute personAttribute : person.getAttributes()) {
            mrsUser.addAttribute(new Attribute(personAttribute.getAttributeType().getName(), personAttribute.getValue()));
        }
        return mrsUser;
    }

    private Map save(MRSUser mrsUser) throws UserAlreadyExistsException {
        org.openmrs.User user = new org.openmrs.User();
        Person person = new Person();
        PersonName personName = new PersonName(mrsUser.getFirstName(), mrsUser.getMiddleName(), mrsUser.getLastName());
        person.addName(personName);
        person.setGender(PERSON_UNKNOWN_GENDER);
        final String password = new Password(PASSWORD_LENGTH).create();

        for (Attribute attribute : mrsUser.getAttributes()) {
            PersonAttributeType attributeType = personService.getPersonAttributeTypeByName(attribute.name());
            person.addAttribute(new PersonAttribute(attributeType, attribute.value()));
        }
        Role role = userService.getRole(mrsUser.getSecurityRole());
        user.addRole(role);

        String systemId = mrsUser.getSystemId();

        if(isNotEmpty(mrsUser.getId())) user.setId(parseInt(mrsUser.getId()));
        user.setSystemId(systemId);
        user.setUsername(mrsUser.getUserName());
        user.setPerson(person);

        Map<String, Object> userMap = new HashMap<String, Object>();
        final org.openmrs.User savedUser = userService.saveUser(user, password);
        userMap.put("openMRSUser", savedUser);
        userMap.put("password", password);
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

        String newPassword = new Password(PASSWORD_LENGTH).create();
        try {
            Context.getUserService().changePassword(userByUsername, newPassword);
        } finally {
            Context.closeSession();
        }
        return newPassword;
    }
}

