package org.motechproject.batch.service.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


public class BatchJobClassLoader extends ClassLoader{

	public BatchJobClassLoader(ClassLoader l) {
		super(l);
	}
	
	@Override
	public URL findResource(String name){
		
		URL url = super.findResource(name);
		if (url == null) {
			String absNmae = "/home/beehyv/jobs/" +name;
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
