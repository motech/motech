package org.motechproject.openmrs.services;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.model.UserAttribute;
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
import org.springframework.beans.factory.annotation.Autowired;

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
    public String saveUser(User mrsUser) throws UserAlreadyExistsException {
        org.openmrs.User user = new org.openmrs.User();
        org.openmrs.Person person = new org.openmrs.Person();
        PersonName personName = new PersonName(mrsUser.firstName(), mrsUser.middleName(), mrsUser.lastName());
        person.addName(personName);
        person.setGender(Constants.PERSON_UNKNOWN_GENDER);

        for (UserAttribute attribute : mrsUser.attributes()) {
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

        userService.saveUser(user, new Password(Constants.PASSWORD_LENGTH).create());
        return user.getSystemId();
    }

}
