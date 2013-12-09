package org.motechproject.config.core.domain;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.filters.ConfigFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

/**
 * Defines a MOTECH configuration location. If the given location starts with a leading file separator character,
 * the location is treated as a file system directory. Otherwise, it is treated as a classpath location.
 */
public class ConfigLocation {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLocation.class);

    private String configLocation;

    public ConfigLocation(String configLocation) {
        this.configLocation = configLocation;

        if (!this.configLocation.endsWith(File.separator)) {
            this.configLocation += File.separator;
        }
    }

    /**
     * Resource corresponding to the config location.
     *
     * @return resource
     */
    public Resource toResource() {
        if (configLocation.startsWith(File.separator)) {
            try {
                return getUrlResource();
            } catch (MalformedURLException e) {
                throw new MotechConfigurationException(String.format("Invalid config location %s.", configLocation), e);
            }
        } else {
            return new ClassPathResource(configLocation);
        }
    }

    public String getLocation() {
        return configLocation;
    }

    UrlResource getUrlResource() throws MalformedURLException {
        return new UrlResource(String.format("file:%s", configLocation));
    }

    /**
     * <p>
     * This method Returns the {@link java.io.File} object for the given file name relative to the config location.
     * It also checks for the requested file accessibility. If the requested access type check is
     * {@link ConfigLocation.FileAccessType.READABLE}, the file's existence and readability will be checked.
     * Similarly, if the requested access type check is {@link ConfigLocation.FileAccessType.WRITABLE}, then the
     * write accessibility to the file will be checked. If the file does not exists, write accessibility of its
     * ancestors will be checked.
     * </p>
     *
     * @param fileName   Name of the file to be added to the config location.
     * @param accessType One of {@link ConfigLocation.FileAccessType.READABLE} or {@link ConfigLocation.FileAccessType.WRITABLE}.
     * @return File relative to the config location.
     * @throws MotechConfigurationException if the file is not readable or writable depending on the given access type.
     */
    public File getFile(String fileName, FileAccessType accessType) {
        try {
            Resource resource = toResource().createRelative(fileName);
            if (accessType.isAccessible(resource)) {
                return resource.getFile();
            }

            throw new MotechConfigurationException(String.format("%s file in the location %s is not %s.", fileName, configLocation, accessType.toString()));
        } catch (IOException e) {
            throw new MotechConfigurationException(String.format("Error while checking if file %s in the location %s is %s.", fileName, configLocation, accessType.toString()), e);
        }
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "ConfigLocation{" +
                "configLocation='" + configLocation + '\'' +
                '}';
    }

    public List<File> getExistingConfigFiles() {
        List<File> files = (List<File>) FileUtils.listFiles(new File(configLocation), new ConfigFileFilter(), TrueFileFilter.INSTANCE);
        LOGGER.debug(String.format("Found existing files. %s", files));
        return files;
    }

    public boolean hasPlatformConfigurationFile() {
        Collection collection = FileUtils.listFiles(new File(configLocation), ConfigFileFilter.PLATFORM_CORE_CONFIG_FILTER, null);
        return !collection.isEmpty();
    }

    /**
     * Defines the access check required.
     */
    public static enum FileAccessType {
        READABLE {
            @Override
            boolean isAccessible(Resource resource) {
                return resource.isReadable();
            }
        }, WRITABLE {
            @Override
            boolean isAccessible(Resource resource) throws IOException {
                File file = resource.getFile();

                while (!file.exists()) {
                    file = file.getParentFile();
                }

                return file.canWrite();
            }
        };

        abstract boolean isAccessible(Resource resource) throws IOException;
    }
}
