package org.motechproject.demo.commcare.domain;

import java.util.List;
import java.util.Map;

public class CommcareUser {

	private String default_phone_number;
	private String email;
	private String first_name;
	private String id;
	private String last_name;
	private String resource_ui;
	private String username;
	
	List<String> groups;
	Map<String, String> user_data;
	List<String> phone_numbers;
	
	public List<String> getPhoneNumbers() {
		return phone_numbers;
	}
	
	public String getDefaultPhoneNumber() {
		return default_phone_number;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getFirstName() {
		return first_name;
	}
	
	public String getId() {
		return id;
	}
	
	public String getLastName() {
		return last_name;
	}
	
	public String getResourceUi() {
		return resource_ui;
	}
	
	public String getUsername() {
		return username;
	}
	
	public List<String> getGroups() {
		return groups;
	}
	
	public Map<String, String> getUserData() {
		return user_data;
	}
	
	
	
	
}
