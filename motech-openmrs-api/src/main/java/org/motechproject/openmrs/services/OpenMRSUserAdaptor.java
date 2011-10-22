package org.motechproject.openmrs.services;

import org.motechproject.mrs.model.User;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.mrs.services.MRSUserAdaptor;
import org.motechproject.openmrs.model.Constants;
import org.motechproject.openmrs.model.Password;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

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
    public String saveUser(User mrsUser) {
        org.openmrs.User user = new org.openmrs.User();
        org.openmrs.Person person = new org.openmrs.Person();

        PersonName personName = new PersonName(mrsUser.firstName(), mrsUser.middleName(), mrsUser.lastName());
        person.addName(personName);
        addAttribute(person, Constants.PERSON_ATTRIBUTE_PHONE_NUMBER, mrsUser.phoneNumber());
        addAttribute(person, Constants.PERSON_ATTRIBUTE_STAFF_TYPE, mrsUser.role());
        addAttribute(person, Constants.PERSON_ATTRIBUTE_EMAIL, mrsUser.email());

        Role role = userService.getRole(OpenmrsConstants.PROVIDER_ROLE);
        user.setSystemId(UUID.randomUUID().toString());
        user.setPerson(person);
        user.addRole(role);

        userService.saveUser(user, new Password(Constants.PASSWORD_LENGTH).create());
        return user.getSystemId();
    }

    private void addAttribute(Person person, String name, String value) {
        PersonAttributeType attributeType = personService.getPersonAttributeTypeByName(name);
        person.addAttribute(new PersonAttribute(attributeType, value));
    }


}
