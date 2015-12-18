package org.motechproject.server.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.osgi.web.util.OSGiServiceUtils;
import org.motechproject.server.config.domain.MotechSettings;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * SettingsFacade provides an interface to access application configuration present in files or database.
 */
public class SettingsFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsFacade.class);
    private static final int CONFIG_SERVICE_WAIT_TIME = 10000; // 10s

    private ConfigurationService configurationService;

    private boolean rawConfigRegistered;
    private boolean propsRegistered;

    private Map<String, Properties> config = new HashMap<>();
    private Map<String, Resource> rawConfig = new HashMap<>();
    private Map<String, Properties> defaultConfig = new HashMap<>();

    private Bundle bundle;
    private BundleContext bundleContext;

    public String getBundleSymbolicName() {
        return bundle != null ? bundle.getSymbolicName() : "";
    }

    public String getBundleVersion() {
        return (bundle != null && bundle.getVersion() != null) ? bundle.getVersion().toString() : "";
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.bundle = bundleContext != null ? bundleContext.getBundle() : null;
    }

    @PostConstruct
    public void afterPropertiesSet() {
        LOGGER.debug("SettingsFacade in bundle {} started searching for ConfigurationService.", bundleContext.getBundle().getSymbolicName());
        configurationService = OSGiServiceUtils.findService(bundleContext, ConfigurationService.class,
                CONFIG_SERVICE_WAIT_TIME);

        LOGGER.debug("ConfigurationService for SettingsFacade in bundle {} {}", bundleContext.getBundle().getSymbolicName(),
                (configurationService == null ? "was not found." : "was found."));
        if (configurationService == null) {
            throw new MotechConfigurationException("SettingsFacade in bundle " + getBundleSymbolicName() +
                    " is unable to retrieve ConfigurationService");
        }

        registerConfigurationSettings();
    }

    public void setConfigFiles(List<Resource> resources) {
        for (Resource configFile : resources) {
            InputStream is = null;
            try {
                is = configFile.getInputStream();

                Properties props = new Properties();
                props.load(is);

                config.put(getResourceFileName(configFile), props);
                defaultConfig.put(getResourceFileName(configFile), props);

            } catch (IOException e) {
                throw new MotechException("Cant load config file " + configFile.getFilename(), e);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        registerAllProperties();
    }

    public void setRawConfigFiles(List<Resource> resources) {
        for (Resource resource : resources) {
            rawConfig.put(getResourceFileName(resource), resource);
        }
        registerAllRawConfig();
    }


    public String getProperty(String key) {
        String result = null;
        String filename = findFilename(key);

        if (filename != null) {
            result = getProperty(key, filename);
        }

        return result;
    }

    /**
     * Returns a value of a property with given key, stored in a resource with given filename.
     *
     * @param key  the name of the property
     * @param filename  the resource filename
     * @return property value as {@code String}
     */
    public String getProperty(String key, String filename) {
        String result = null;
        Properties props = getProperties(filename);
        if (props != null) {
            result = props.getProperty(key);
        }
        return result;
    }

    /**
     * Returns properties from a resource with given filename.
     *
     * @param filename  the resource filename
     * @return properties stored in the file
     */
    public Properties getProperties(String filename) {
        if (propsRegistered) {
            try {
                Properties p = configurationService.getBundleProperties(getBundleSymbolicName(), filename, defaultConfig.get(filename));
                config.put(filename, p);
            } catch (IOException e) {
                throw new MotechException("Can't read settings", e);
            }
        }

        Properties result = config.get(filename);
        if (result == null) {
            result = defaultConfig.get(filename);
        }
        return (result == null ? new Properties() : result);
    }


    public void setProperty(String key, String value) {
        String filename = findFilename(key);

        if (filename == null) {
            throw new MotechException("No file containing key " + key);
        }

        setProperty(filename, key, value);
    }

    /**
     * Saves given properties and resource filename to the configuration. If configuration properties stored in this
     * object were already registered to the configuration service, the given properties and resource filename will also
     * be added there.
     *
     * @param filename  the resource filename
     * @param properties  the properties to be saved
     * @throws org.motechproject.commons.api.MotechException when I/O error occurs
     */
    public void saveConfigProperties(String filename, Properties properties) {
        config.put(filename, properties);
        if (propsRegistered) {
            try {
                configurationService.addOrUpdateProperties(getBundleSymbolicName(), getBundleVersion(), filename,
                        properties, defaultConfig.get(filename));
            } catch (IOException e) {
                throw new MotechException("Can't save settings " + filename, e);
            }

        }
    }

    /**
     * Allows persisting of raw JSON properties either in the database or file.
     *
     * @param filename  resource filename
     * @param resource  resource data to persist
     * @throws org.motechproject.commons.api.MotechException when I/O error occurs
     */
    public void saveRawConfig(String filename, Resource resource) {
        rawConfig.put(filename, resource);
        try (InputStream is = resource.getInputStream()) {
            configurationService.saveRawConfig(getBundleSymbolicName(), getBundleVersion(), filename, is);
        } catch (IOException e) {
            throw new MotechException("Error saving file " + filename, e);
        }
    }

    /**
     *  Allows persisting of raw JSON properties either in the database or file.
     *
     * @param filename json filename
     * @param jsonText json data to persist
     * @throws org.motechproject.commons.api.MotechException when I/O error occurs
     */
    public void saveRawConfig(String filename, String jsonText) {
        try {
            configurationService.saveRawConfig(getBundleSymbolicName(), getBundleVersion(), filename, IOUtils.toInputStream(jsonText));
        } catch (IOException e) {
            throw new MotechException("Error saving file " + filename, e);
        }
    }

    public String propertiesToJson(Properties props) {
        Gson gson = new GsonBuilder().create();
        Map<String, String> map = new HashMap<String, String>();
        for (Entry<Object, Object> prop : props.entrySet()) {
            map.put((String) prop.getKey(), (String) prop.getValue());
        }
        return gson.toJson(map);
    }

    public Properties getPropertiesFromRawConfigFile(String filename) {
        String json = new String();
        try {
            json = IOUtils.toString(getRawConfig(filename));
        } catch (Exception ex) {
            return new Properties();
        }
        Gson gson = new GsonBuilder().create();
        Type typeOfHashMap = new TypeToken<Map<String, String>>() { } .getType();
        Map<String, String> map = gson.fromJson(json, typeOfHashMap);
        Properties props = new Properties();
        if (map != null) {
            for (Entry<String, String> entry : map.entrySet()) {
                props.put(entry.getKey(), entry.getValue());
            }
        } else {
            return new Properties();
        }
        return props;
    }

    /**
     * Allows to retrieve raw JSON data either from the database or file.
     *
     * @param filename Resource filename
     * @return Raw JSON data as InputStream
     * @throws org.motechproject.commons.api.MotechException when I/O error occurs
     */
    public InputStream getRawConfig(String filename) {
        InputStream is = null;

        if (rawConfigRegistered) {
            // read from platform
            try {
                is = configurationService.getRawConfig(getBundleSymbolicName(), filename, rawConfig.get(filename));
            } catch (IOException e) {
                throw new MotechException("Error loading file " + filename, e);
            }
        } else {
            // read resource
            Resource resource = rawConfig.get(filename);
            if (resource != null) {
                try {
                    is = resource.getInputStream();
                } catch (IOException e) {
                    throw new MotechException("Error reading raw config", e);
                }
            }
        }

        return is;
    }

    /**
     * Converts stored configuration to {@code Properties}.
     *
     * @return the configuration as {@code Properties}
     */
    public Properties asProperties() {
        Properties result = new Properties();
        for (Properties p : config.values()) {
            result.putAll(p);
        }
        return result;
    }

    /**
     * Registers all the properties to the configuration service.
     */
    protected void registerAllProperties() {
        if (configurationService != null) {
            for (Map.Entry<String, Properties> entry : config.entrySet()) {
                String filename = entry.getKey();
                Properties props = entry.getValue();

                registerProperties(filename, props);
            }
            propsRegistered = true;
        }
    }

    /**
     * Registers properties from file with given name to the configuration service.
     *
     * @param filename  the name of the file with properties
     * @param properties  properties to be registered
     */
    protected void registerProperties(String filename, Properties properties) {
        if (configurationService != null) {
            try {
                if (!configurationService.registersProperties(getBundleSymbolicName(), filename)) {
                    configurationService.addOrUpdateProperties(
                            getBundleSymbolicName(), getBundleVersion(), filename, properties, defaultConfig.get(filename));
                } else if (configurationService.registersProperties(getBundleSymbolicName(), filename)) {
                    configurationService.updatePropertiesAfterReinstallation(getBundleSymbolicName(), getBundleVersion(),
                            filename, defaultConfig.get(filename), properties);
                }

                Properties registeredProps = configurationService.getBundleProperties(
                        getBundleSymbolicName(), filename, defaultConfig.get(filename));
                config.put(filename, registeredProps);
            } catch (IOException e) {
                throw new MotechException("Cant register settings", e);
            }
        }
    }

    /**
     * Unregisters properties of the bundle with given symbolic name.
     *
     * @param symbolicName  the symbolic name of the bundle
     */
    public void unregisterProperties(String symbolicName) {
        configurationService.removeAllBundleProperties(symbolicName);
    }

    /**
     * Registers all raw configurations to the configuration service.
     */
    protected void registerAllRawConfig() {
        if (configurationService != null) {
            for (Map.Entry<String, Resource> entry : rawConfig.entrySet()) {
                String filename = entry.getKey();
                Resource resource = entry.getValue();

                if (!configurationService.rawConfigExists(getBundleSymbolicName(), filename)) {
                    // register new config with the platform
                    try {
                        InputStream is = resource.getInputStream();
                        configurationService.saveRawConfig(getBundleSymbolicName(), getBundleVersion(), filename, is);
                    } catch (IOException e) {
                        throw new MotechException("Can't save raw config " + filename, e);
                    }
                }
            }
            rawConfigRegistered = true;
        }
    }

    /**
     * Returns a name of a file containing given property.
     *
     * @param key  the property name
     * @return the name of a file
     */
    protected String findFilename(String key) {
        for (Map.Entry<String, Properties> entry : config.entrySet()) {
            Properties props = entry.getValue();
            String filename = entry.getKey();

            if (props.containsKey(key)) {
                return filename;
            }
        }

        for (Map.Entry<String, Properties> entry : defaultConfig.entrySet()) {
            Properties props = entry.getValue();
            String filename = entry.getKey();

            if (props.containsKey(key)) {
                return filename;
            }
        }
        return null;
    }

    /**
     * Returns a name of resource file
     *
     * @param resource the resource file
     * @return the file name of the resource
     */
    protected static String getResourceFileName(Resource resource) {
        String name = resource.getFilename();

        if (resource instanceof ClassPathResource) {
            name = ((ClassPathResource) resource).getPath();
        } else {
            int colonIndex = name.indexOf(':');
            if (colonIndex >= 0) {
                name = name.substring(colonIndex + 1);
            }
        }

        return name;
    }

    private void registerConfigurationSettings() {
        if (!propsRegistered) {
            registerAllProperties();
        }
        if (!rawConfigRegistered) {
            registerAllRawConfig();
        }
    }


    private void setProperty(String filename, String key, String value) {
        if (!config.containsKey(filename)) {
            config.put(filename, new Properties());
        }

        Properties props = config.get(filename);
        props.put(key, value);
        saveConfigProperties(filename, props);
    }

    public MotechSettings getPlatformSettings() {
        return configurationService.getPlatformSettings();
    }

    /**
     * Saves given MOTECH settings to the configuration service.
     *
     * @param settings  the {@code MotechSettings} to be saved
     */
    public void savePlatformSettings(MotechSettings settings) {
        configurationService.savePlatformSettings(settings);
    }

    /**
     * Checks if configuration settings have been registered.
     *
     * @return true if setting have been registered, false otherwise
     */
    public boolean areConfigurationSettingsRegistered() {
        return propsRegistered && rawConfigRegistered;
    }
}
