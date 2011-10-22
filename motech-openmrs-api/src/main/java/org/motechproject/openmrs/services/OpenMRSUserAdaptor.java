package org.motechproject.openmrs.services;

import org.motechproject.mrs.services.MRSException;
import org.motechproject.mrs.services.MRSUserAdaptor;
import org.openmrs.api.APIException;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenMRSUserAdaptor implements MRSUserAdaptor {

    private org.openmrs.api.UserService userService;

    @Autowired
    public OpenMRSUserAdaptor(org.openmrs.api.UserService userService) {
        this.userService = userService;
    }

    public void changeCurrentUserPassword(String currentPassword, String newPassword) {
        try {
            userService.changePassword(currentPassword, newPassword);
        } catch (APIException e) {
            throw new MRSException(e);
        }
    }
}
