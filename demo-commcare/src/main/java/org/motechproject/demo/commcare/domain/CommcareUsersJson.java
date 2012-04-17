package org.motechproject.demo.commcare.domain;

import java.util.List;
import java.util.Map;

public class CommcareUsersJson {

	Map<String, String> meta;
	
	List<CommcareUser> objects;
	
	public List<CommcareUser> getObjects() {
		return objects;
	}
	
	public Map<String, String> getMeta() {
		return meta;
	}
}
