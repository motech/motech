package org.motechproject.commcare.service;

import java.util.List;

import org.motechproject.commcare.domain.CommcareUser;


public interface CommcareUserService {

    public List<CommcareUser> getAllUsers();

    public CommcareUser getCommcareUserById(String id);


}
