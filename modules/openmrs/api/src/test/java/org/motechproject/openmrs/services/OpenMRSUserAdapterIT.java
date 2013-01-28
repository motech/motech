package org.motechproject.openmrs.services;

import org.junit.Test;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.OpenMRSAttribute;
import org.motechproject.mrs.model.OpenMRSUser;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.services.UserAdapter;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectUnique;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;

public class OpenMRSUserAdapterIT extends OpenMRSIntegrationTestBase {

    @Autowired
    UserAdapter mrsUserAdapter;
    @Autowired
    UserService userService;

    @Test
    @Transactional(readOnly = true)
    public void shouldSaveUser() throws UserAlreadyExistsException {
        OpenMRSUser mrsUser = new OpenMRSUser();
        OpenMRSPerson person = new OpenMRSPerson();
        String lastName = "Last";
        String middleName = "Middle";
        String firstName = "First";
        String address = "No.1, 1st Street, Ghana - 1";
        String email = "a@b.com";
        String securityRole = "System Developer";
        String phoneNumber = "0987654321";

        person.firstName(firstName).middleName(middleName).lastName(lastName).address(address)
                .addAttribute(new OpenMRSAttribute("Email", email)).addAttribute(new OpenMRSAttribute("Phone Number", phoneNumber));
        mrsUser.person(person).userName(email).securityRole(securityRole);
        final Map userData = mrsUserAdapter.saveUser(mrsUser);

        assertEquals(2, userData.size());
        final OpenMRSUser user = (OpenMRSUser) userData.get(OpenMRSUserAdapter.USER_KEY);

        assertEquals(firstName, user.getPerson().getFirstName());
        assertEquals(middleName, user.getPerson().getMiddleName());
        assertEquals(lastName, user.getPerson().getLastName());
        assertEquals(securityRole, user.getSecurityRole());

        OpenMRSAttribute actualEmail = selectUnique(user.getPerson().getAttributes(), having(on(OpenMRSAttribute.class).name(), equalTo("Email")));
        OpenMRSAttribute actualPhone = selectUnique(user.getPerson().getAttributes(), having(on(OpenMRSAttribute.class).name(), equalTo("Phone Number")));

        assertEquals(email, actualEmail.value());
        assertEquals(email, user.getUserName());
        assertEquals(phoneNumber, actualPhone.value());
    }
}
