package org.motechproject.mrs.services;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

public interface MRSUserAdaptor {
    void changeCurrentUserPassword(String currentPassword, String newPassword);
    Map saveUser(MRSUser mrsUser) throws UserAlreadyExistsException;
    String setNewPasswordForUser(String emailID) throws UsernameNotFoundException;
    List<MRSUser> getAllUsers();
    MRSUser getUserBySystemId(String id);
    Map updateUser(MRSUser mrsUser) throws UserAlreadyExistsException;
}
