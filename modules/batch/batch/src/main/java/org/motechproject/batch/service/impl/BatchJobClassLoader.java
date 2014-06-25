package org.motechproject.batch.service.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


public class BatchJobClassLoader extends ClassLoader{

	private String location;
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public BatchJobClassLoader(ClassLoader l, String location) {
		super(l);
		this.location = location;
	}
	
	@Override
	public URL findResource(String name){
		
		URL url = super.findResource(name);
		if (url == null) {
			String absNmae = location + name;//"/home/beehyv/jobs/" +name;
			try {
				File file = new File(absNmae);
				if(file.exists()) {
					url = new File(absNmae).toURI().toURL();
				}
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
				return null;
			}
		} 
		return url;
	}
}
