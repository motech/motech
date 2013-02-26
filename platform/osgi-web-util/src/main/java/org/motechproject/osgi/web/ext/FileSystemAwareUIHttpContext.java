package org.motechproject.osgi.web.ext;

import org.motechproject.commons.api.MotechException;
import org.motechproject.osgi.web.UiHttpContext;
import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.String.format;

public class FileSystemAwareUIHttpContext extends UiHttpContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemAwareUIHttpContext.class);

    private String resourceRootDirectoryPath;

    public FileSystemAwareUIHttpContext(HttpContext context, String resourceRootDirectoryPath) {
        super(context);
        this.resourceRootDirectoryPath = resourceRootDirectoryPath;
    }

    @Override
    public URL getResource(String name) {
        String resourcePath = new StringBuilder(resourceRootDirectoryPath).append("/").append(name).toString();
        File resourceFile = new File(resourcePath);
        LOGGER.info("Using FileSystemAwareUIHttpContext to deliver resource " + resourcePath);
        if (resourceFile.exists()) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    print(resourceFile);
                }
                return resourceFile.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new MotechException(format("Exception when try to resolve %s", resourcePath), e);
            }
        }
        LOGGER.warn("Resource " + resourcePath + " does not exist");
        return getContext().getResource(name);
    }

    private void print(File resourceFile) {
        LOGGER.debug("Dumping " + resourceFile.getName());
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(resourceFile));
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                LOGGER.debug(currentLine);
            }
        } catch (Exception e) {
            LOGGER.warn(String.format("%s could not be written to logs ", resourceFile.getName()));
        }
        LOGGER.debug("Done ");
    }

    public String getResourceRootDirectoryPath() {
        return resourceRootDirectoryPath;
    }
}
