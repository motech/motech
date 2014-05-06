package org.motechproject.batch.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.motechproject.batch.exception.ApplicationErrors;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.repository.JobRepository;
import org.motechproject.batch.service.FileUploadService;
import org.motechproject.batch.web.BatchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
//@Transactional
public class FileUploadServiceImpl implements FileUploadService {

//@Autowired
JobRepository jobRepo;



private final static Logger LOGGER = Logger.getLogger(BatchController.class);
	
	
	public JobRepository getJobRepo() {
		return jobRepo;
	}

	public void setJobRepo(JobRepository jobRepo) {
		this.jobRepo = jobRepo;
	}

	
	
	@Override
	public void uploadFile(String jobName, MultipartFile file,String xmlPath)
			throws BatchException {
		
		boolean jobExists = jobRepo.checkBatchJob(jobName);
		
		if(jobExists == false)
			throw new BatchException(ApplicationErrors.JOB_NOT_FOUND);
		
		LOGGER.debug("xml path"+xmlPath);
		byte[] bytes;
		BufferedOutputStream stream;
		
		try {
			bytes = file.getBytes();
			stream = new BufferedOutputStream(new FileOutputStream(new File(xmlPath,jobName+".xml")));
			stream.write(bytes);
		    stream.close();
		
		} catch (IOException e) {
			throw new BatchException(ApplicationErrors.FILE_READING_WRTING_FAILED, e.getMessage());
		}
		
		
	}
	
	
	

}
