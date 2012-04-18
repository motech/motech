package org.motechproject.demo.commcare.services.impl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.demo.commcare.domain.CommcareUser;
import org.motechproject.demo.commcare.domain.CommcareUsersJson;
import org.motechproject.demo.commcare.services.CommcareUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

@Service
public class CommcareUserServiceImpl implements CommcareUserService{

    private MotechJsonReader motechJsonReader;
	
    private HttpClient commonsHttpClient;
    
    private Properties commcareUserProperties;
	
	@Autowired
	public CommcareUserServiceImpl(HttpClient commonsHttpClient, @Qualifier(value = "commcareUserApi")Properties commcareUserProperties) {
		this.commonsHttpClient = commonsHttpClient;
		this.commcareUserProperties = commcareUserProperties;
		this.motechJsonReader = new MotechJsonReader();
	}
    
	@Override
	public List<CommcareUser> getAllUsers() {
			
		HttpMethod getMethod = new GetMethod(commcareUrl());
		
		commonsHttpClient.getParams().setAuthenticationPreemptive(true);
		
		commonsHttpClient.getState()
			.setCredentials(new AuthScope(null, -1, null, null),
					new UsernamePasswordCredentials(getUsername(), 
							getPassword()));
  
		String response = "";
		
		try {
			int status = commonsHttpClient.executeMethod(getMethod);
			response = getMethod.getResponseBodyAsString();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Type commcareUserType = new TypeToken<CommcareUsersJson>() {}.getType();
        
        CommcareUsersJson allUsers = (CommcareUsersJson) motechJsonReader.readFromString(response, commcareUserType);
		
        return allUsers.getObjects();
		
	}

	@Override
	public CommcareUser getCommcareUserById(String id) {
		List<CommcareUser> userList = getAllUsers();
		for (CommcareUser user : userList) {
			if (user.getId().equals(id)) return user;
		}
		return null;
	}
	
	private String commcareUrl() {
		return "https://www.commcarehq.org/a/" + 
				getCommcareDomain() + 
				"/api/v0.1/user/?format=json";
	}
	
	private String getCommcareDomain() {
		return commcareUserProperties.getProperty("commcareDomain");
	}
	
	private String getUsername() {
		return commcareUserProperties.getProperty("username");
	}
	
	private String getPassword() {
		return commcareUserProperties.getProperty("password");
	}

	
	
}
