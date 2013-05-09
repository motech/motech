package org.motechproject.couch.mrs.impl;

import org.motechproject.mrs.domain.MRSUser;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.services.MRSUserAdapter;

import java.util.List;
import java.util.Map;

public class StubCouchUserAdapter implements MRSUserAdapter {
    @Override
    public void changeCurrentUserPassword(String currentPassword, String newPassword) {
        throw new UnsupportedOperationException();    
    }

    @Override
    public Map<String, Object> saveUser(MRSUser mrsUser) throws UserAlreadyExistsException {
        throw new UnsupportedOperationException();  
    }

    @Override
    public String setNewPasswordForUser(String userId) {
        throw new UnsupportedOperationException();  
    }

    @Override
    public List<MRSUser> getAllUsers() {
        throw new UnsupportedOperationException();  
    }

    @Override
    public MRSUser getUserByUserName(String userId) {
        throw new UnsupportedOperationException();  
    }

    @Override
    public Map<String, Object> updateUser(MRSUser mrsUser) {
        throw new UnsupportedOperationException();  
    }
}
