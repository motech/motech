package org.motechproject.batch.controller;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class MctsFileUploadController {
	
	    //@Value("#{serverHome.serverhome}")
		
	  

	

	/* @Value("#{serverHome.motechXmlContainer}")
		private String MCTS_XML_CONTAINER;*/
	   @RequestMapping(value="/upload", method=RequestMethod.GET)
	    public @ResponseBody String provideUploadInfo() {
	        return "You can upload a file by posting to this same URL.";
	    }
	   
	    @RequestMapping(value="/upload", method=RequestMethod.POST)
	    public String handleFileUpload(
	            @RequestParam (value="file", required= false) MultipartFile file){
	    	
	    	String fileName = file.getOriginalFilename();
	    	File uploadedFile = null;
	    	final String uploadDir   = System.getProperty("java.io.tmpdir");
	    	// TODO To store the path in properties file.
	        uploadedFile = new File(uploadDir, fileName);
	            try {
	                byte[] bytes = file.getBytes();
	                BufferedOutputStream stream =
	                        new BufferedOutputStream(new FileOutputStream(uploadedFile));
	                stream.write(bytes);
	                stream.close();
	              
	            } catch (Exception e) {
	              
	            }
	        
	            return "";
             

       
	    }

	}
