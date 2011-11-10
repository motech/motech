package org.motechproject.openmrs.services;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.mrs.services.MRSUserAdaptor;
import org.motechproject.openmrs.model.Constants;
import org.motechproject.openmrs.model.Password;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;

import static org.apache.commons.lang.StringUtils.isNotBlank;

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
    public HashMap saveUser(User mrsUser) throws UserAlreadyExistsException {
        org.openmrs.User user = new org.openmrs.User();
        org.openmrs.Person person = new org.openmrs.Person();
        PersonName personName = new PersonName(mrsUser.firstName(), mrsUser.middleName(), mrsUser.lastName());
        person.addName(personName);
        person.setGender(Constants.PERSON_UNKNOWN_GENDER);
        final String password = new Password(Constants.PASSWORD_LENGTH).create();

        for (Attribute attribute : mrsUser.attributes()) {
            PersonAttributeType attributeType = personService.getPersonAttributeTypeByName(attribute.name());
            person.addAttribute(new PersonAttribute(attributeType, attribute.value()));
        }
        Role role = userService.getRole(mrsUser.securityRole());
        user.addRole(role);

        String id = mrsUser.id();
        String userId = isNotBlank(id) ? id : userService.generateSystemId();
        if (userService.getUserByUsername(id) != null) throw new UserAlreadyExistsException();
        user.setSystemId(userId);
        user.setPerson(person);

        HashMap userMap = new HashMap();
        userMap.put("userLoginId",user.getSystemId());
        userMap.put("password",password);

        userService.saveUser(user, password);
        return userMap;
    }

    @Override
    public String setNewPasswordForUser(String emailID) throws UsernameNotFoundException {

        org.openmrs.User userByUsername;
        Context.openSession();
        Context.authenticate("admin","P@ssw0rd");
        try{
            userByUsername = userService.getUserByUsername(emailID);
        }catch(Exception e)
        {
            Context.closeSession();
            throw new UsernameNotFoundException("User was not found");
        }

        String newPassword = new Password(Constants.PASSWORD_LENGTH).create();
        try {
            Context.getUserService().changePassword(userByUsername,newPassword);
        } finally {
            Context.closeSession();
        }
        return newPassword;
    }
}

