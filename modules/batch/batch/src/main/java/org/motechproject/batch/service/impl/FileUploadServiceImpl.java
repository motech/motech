package org.motechproject.batch.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.motechproject.batch.exception.ApplicationErrors;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.mds.BatchJob;
import org.motechproject.batch.mds.service.BatchJobMDSService;
import org.motechproject.batch.service.FileUploadService;
import org.motechproject.batch.web.BatchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service(value = "fileUploadService")
@Transactional
public class FileUploadServiceImpl implements FileUploadService {




private final static Logger LOGGER = Logger.getLogger(BatchController.class);
	
	
	private BatchJobMDSService jobRepo;

	@Autowired
	public FileUploadServiceImpl(BatchJobMDSService jobRepo) {
		this.jobRepo = jobRepo;
	}
	
	@Override
	public void uploadFile(String jobName, MultipartFile file,String xmlPath)
			throws BatchException {
		
		List<BatchJob> batchJobList = jobRepo.findByJobName(jobName);
		boolean jobExists = true;
		if(batchJobList == null || batchJobList.size() == 0) {
			jobExists = false;
		}
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
