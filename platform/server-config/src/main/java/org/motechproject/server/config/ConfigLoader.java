package org.motechproject.server.config;

import org.apache.commons.io.FileUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class ConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);

    private List<Resource> configLocations;
    private File configLocationsFile = new File(String.format("%s/.motech/config-locations.conf", System.getProperty("user.home")));

    @Autowired
    private ResourceLoader resourceLoader;

    public ConfigLoader() throws IOException {
        if (configLocationsFile.exists()) {
            load();
        } else {
            LOGGER.warn(String.format("%s not found. Using default locations for config file.", configLocationsFile.getAbsolutePath()));

            this.configLocations = new ArrayList<>(2);
            this.configLocations.add(new UrlResource(String.format("file:%s/.motech/config/", System.getProperty("user.home"))));
            this.configLocations.add(new UrlResource("file:/etc/motech/"));
        }
    }

    public ConfigLoader(List<Resource> configLocations) {
        this.configLocations = configLocations;
    }

    public void addConfigLocation(String location) {
        Resource resource = resourceLoader.getResource(location);
        addConfigLocation(resource);
    }

    public void addConfigLocation(Resource location) {
        if (configLocations == null) {
            configLocations = new ArrayList<>();
        }

        configLocations.add(0, location);
    }

    public ConfigFileSettings loadConfig() {
        ConfigFileSettings configFileSettings = null;

        if (configLocations != null) {
            for (Resource location : configLocations) {
                try {
                    Resource motechSettings = location.createRelative(PlatformSettingsService.SETTINGS_FILE_NAME);
                    if (!motechSettings.isReadable()) {
                        LOGGER.warn("Could not read motech-settings.conf from: " + location.toString());
                        continue;
                    }

                    Resource activemq = location.createRelative(PlatformSettingsService.ACTIVEMQ_FILE_NAME);
                    if (!activemq.isReadable()) {
                        LOGGER.warn("No activemq.properties file found at: " + location.toString());
                        LOGGER.warn("Using default activemq.properties file");
                        activemq = resourceLoader.getResource("classpath:default-activemq.properties");
                    }

                    configFileSettings = loadSettingsFromStream(motechSettings, activemq);
                    configFileSettings.setFileURL(location.getURL());
                    break;
                } catch (IOException e) {
                    LOGGER.warn("Problem reading motech-settings.conf from location: " + location.toString());
                    continue;
                }
            }
        }

        return configFileSettings;
    }

    public ConfigFileSettings loadDefaultConfig() {
        ConfigFileSettings configFileSettings = null;
        Resource defaultSettings = resourceLoader.getResource("classpath:default-settings.conf");
        Resource defaultActivemq = resourceLoader.getResource("classpath:default-activemq.properties");
        if (defaultSettings != null) {
            configFileSettings = loadSettingsFromStream(defaultSettings, defaultActivemq);
        }

        return configFileSettings;
    }

    public void save() throws IOException {
        StringBuilder sb = new StringBuilder();

        for (Resource config : configLocations) {
            sb.append(config.getURI()).append("\n");
        }

        FileUtils.writeStringToFile(configLocationsFile, sb.toString());
    }

    public static ConfigFileSettings loadSettingsFromStream(Resource motechSettings, Resource activemq) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            try (DigestInputStream dis = new DigestInputStream(motechSettings.getInputStream(), digest);
                    DigestInputStream dis2 = new DigestInputStream(activemq.getInputStream(), digest)) {
                //load configFileSettings and calculate MD5 hash
                ConfigFileSettings configFileSettings = new ConfigFileSettings();
                configFileSettings.load(dis);
                configFileSettings.loadActiveMq(dis2);
                configFileSettings.setMd5checksum(digest.digest());
                return configFileSettings; // startup loaded
            } catch (IOException e) {
                throw new MotechException("Error loading configuration", e);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new MotechException("MD5 algorithm not available", e);
        }
    }

    private void load() throws IOException {
        if (configLocations == null) {
            configLocations = new ArrayList<>();
        }

        try (Scanner scanner = new Scanner(configLocationsFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith("file:")) {
                    this.configLocations.add(new UrlResource(line));
                } else {
                    this.configLocations.add(resourceLoader.getResource(line));
                }
            }
        }
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
