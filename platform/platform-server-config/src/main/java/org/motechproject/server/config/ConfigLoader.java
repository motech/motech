package org.motechproject.server.config;

import org.apache.commons.io.FileUtils;
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
import java.io.InputStream;
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
            this.configLocations.add(new UrlResource(String.format("file:%s/.motech/config/motech-settings.conf", System.getProperty("user.home"))));
            this.configLocations.add(new UrlResource("file:/etc/motech/motech-settings.conf"));
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
                if (location.isReadable()) {
                    try {
                        configFileSettings = loadSettingsFromStream(location.getInputStream());
                        configFileSettings.setFileURL(location.getURL());
                        break; // startup found
                    } catch (IOException e) {
                        LOGGER.error("Error loading config " + location.getFilename(), e);
                    }
                }
            }
        }

        return configFileSettings;
    }

    public void save() throws IOException {
        StringBuilder sb = new StringBuilder();

        for (Resource config : configLocations) {
            sb.append(config.getURI()).append("\n");
        }

        FileUtils.write(configLocationsFile, sb.toString());
    }

    public static ConfigFileSettings loadSettingsFromStream(InputStream is) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            try (DigestInputStream dis = new DigestInputStream(is, digest)) {
                //load configFileSettings and calculate MD5 hash
                ConfigFileSettings configFileSettings = new ConfigFileSettings();
                configFileSettings.load(dis);

                return configFileSettings; // startup loaded
            } catch (IOException e) {
                LOGGER.error("Error loading configuration", e);
                throw new RuntimeException(e);
            }
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("MD5 algorithm not available", e);
            throw new RuntimeException(e);
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
}
