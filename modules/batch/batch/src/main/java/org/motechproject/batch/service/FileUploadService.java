package org.motechproject.batch.service;

import org.motechproject.batch.exception.BatchException;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    void uploadFile(String jobName, MultipartFile file, String xmlPath)
            throws BatchException;

}
