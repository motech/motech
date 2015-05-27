package org.motechproject.osgi.web.ext;

import org.motechproject.commons.api.MotechException;
import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import static java.lang.String.format;

/**
 * An extension of the {@link org.motechproject.osgi.web.ext.UiHttpContext}.
 * This class will be used in development mode for bundles that are configured to load their resources
 * from the hard drive directly, not the jar classpath. The idea is to allow rapid UI development, changes
 * to static html/css/js files will be reflected directly on the UI right after changes are made.
 * If this context fails to load a resource from disk, it will fall back to loading from classpath.
 * This context is a decorator, that decorates the HTTP context coming from Felix.
 *
 * @see org.motechproject.osgi.web.ext.ApplicationEnvironment
 */
public class FileSystemAwareUIHttpContext extends UiHttpContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemAwareUIHttpContext.class);

    private String resourceRootDirectoryPath;

    /**
     * Creates a new instance by decorating the given HTTP context.
     * @param context the context to decorate
     * @param resourceRootDirectoryPath the root path from which this context should attempt to read resources
     */
    public FileSystemAwareUIHttpContext(HttpContext context, String resourceRootDirectoryPath) {
        super(context);
        this.resourceRootDirectoryPath = resourceRootDirectoryPath;
    }

    /**
     * @return the root path from which this context should attempt to read resources
     */
    public String getResourceRootDirectoryPath() {
        return resourceRootDirectoryPath;
    }

    @Override
    public URL getResource(String name) {
        File resourceFile = new File(resourceRootDirectoryPath, name);
        String resourcePath = resourceFile.getAbsolutePath();

        LOGGER.info("Using FileSystemAwareUIHttpContext to deliver resource " + resourcePath);
        if (resourceFile.exists()) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    print(resourceFile);
                }
                return resourceFile.toURI().toURL();
            } catch (IOException e) {
                throw new MotechException(format("Exception when try to resolve %s", resourcePath), e);
            }
        }
        LOGGER.warn("Resource " + resourcePath + " does not exist");
        return getContext().getResource(name);
    }

    private void print(File resourceFile) {
        LOGGER.debug("Dumping " + resourceFile.getName());
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(resourceFile))) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                LOGGER.debug(currentLine);
            }
        } catch (IOException e) {
            LOGGER.warn(String.format("%s could not be written to logs ", resourceFile.getName()), e);
        }
        LOGGER.debug("Done with dump");
    }
}
