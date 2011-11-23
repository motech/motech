package org.motechproject.mrs.services;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MRSUserAdaptor {
    public void changeCurrentUserPassword(String currentPassword, String newPassword);
    public Map saveUser(User mrsUser) throws UserAlreadyExistsException;
    public String setNewPasswordForUser(String emailID) throws UsernameNotFoundException;
    public List<User> getAllUsers();

}
