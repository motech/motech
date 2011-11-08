package org.motechproject.mrs.services;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface MRSUserAdaptor {
    public void changeCurrentUserPassword(String currentPassword, String newPassword);
    public String saveUser(User mrsUser) throws UserAlreadyExistsException;
    public String setNewPasswordForUser(String emailID) throws UsernameNotFoundException;
}
