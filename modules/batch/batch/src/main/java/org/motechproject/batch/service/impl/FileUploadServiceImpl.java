package org.motechproject.batch.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.motechproject.batch.exception.ApplicationErrors;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.service.FileUploadService;
import org.motechproject.batch.web.BatchController;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service(value = "fileUploadService")
@Transactional
public class FileUploadServiceImpl implements FileUploadService {

    private static final Logger LOGGER = Logger
            .getLogger(BatchController.class);

    @Override
    public void uploadFile(String jobName, MultipartFile file, String xmlPath)
            throws BatchException {

        LOGGER.debug("xml path" + xmlPath);
        byte[] bytes;
        BufferedOutputStream stream;

        try {
            bytes = file.getBytes();
            stream = new BufferedOutputStream(new FileOutputStream(new File(
                    xmlPath, jobName + ".xml")));
            stream.write(bytes);
            stream.close();

        } catch (IOException e) {
            throw new BatchException(
                    ApplicationErrors.FILE_READING_WRTING_FAILED, e,
                    ApplicationErrors.FILE_READING_WRTING_FAILED.getMessage());
        }

    }

}
