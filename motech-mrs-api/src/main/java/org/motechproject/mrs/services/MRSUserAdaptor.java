package org.motechproject.mrs.services;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

public interface MRSUserAdaptor {
    void changeCurrentUserPassword(String currentPassword, String newPassword);
    Map saveUser(User mrsUser) throws UserAlreadyExistsException;
    String setNewPasswordForUser(String emailID) throws UsernameNotFoundException;
    List<User> getAllUsers();
    User getUserBySystemId(String id);
    Map updateUser(User mrsUser) throws UserAlreadyExistsException;
}
