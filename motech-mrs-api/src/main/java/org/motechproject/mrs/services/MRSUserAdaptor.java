package org.motechproject.mrs.services;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.User;

public interface MRSUserAdaptor {
    public void changeCurrentUserPassword(String currentPassword, String newPassword);
    public String saveUser(User mrsUser) throws UserAlreadyExistsException;
}
