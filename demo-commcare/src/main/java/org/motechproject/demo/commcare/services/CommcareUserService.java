package org.motechproject.demo.commcare.services;

import java.util.List;

import org.motechproject.demo.commcare.domain.CommcareUser;


public interface CommcareUserService {

	public List<CommcareUser> getAllUsers();
	
	public CommcareUser getCommcareUserById(String id);
	
	
}
