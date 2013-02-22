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
                if (resourcePath.endsWith(".js")) {
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
        LOGGER.info("Dumping " + resourceFile.getName());
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(resourceFile));
            String currentLine = "";
            while ((currentLine = bufferedReader.readLine()) != null) {
                LOGGER.error(currentLine);
            }
        } catch (Exception e) {
            throw new MotechException("File could not be read " + resourceFile.getName(), e);
        }
        LOGGER.info("Done ");
    }

    public String getResourceRootDirectoryPath() {
        return resourceRootDirectoryPath;
    }
}
