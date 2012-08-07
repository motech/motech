package org.motechproject.server.config;

import org.apache.commons.io.IOUtils;
import org.motechproject.MotechException;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SettingsFacade {

    private PlatformSettingsService platformSettingsService;

    @Autowired(required = false)
    private BundleContext bundleContext;

    private Map<String, Properties> config = new HashMap<>();

    @Autowired(required = false)
    public void setPlatformSettingsService(PlatformSettingsService platformSettingsService) {
        this.platformSettingsService = platformSettingsService;
        registerAllProperties();
    }

    public SettingsFacade() { }

    public SettingsFacade(List<Resource> configFiles) {
        for (Resource configFile : configFiles) {
            InputStream is = null;
            try {
                String filename = getResourceFileName(configFile);
                is = configFile.getInputStream();

                Properties props = new Properties();
                props.load(is);

                config.put(filename, props);
            } catch (IOException e) {
                throw new MotechException("Cant load config file " + configFile.getFilename(), e);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }

    public String getProperty(String key) {
        String result = null;
        String filename = findFilename(key);

        if (filename != null) {
            result = getProperty(key, filename);
        }

        return result;
    }

    public String getProperty(String key, String filename) {
        String result = null;
        try {
            if (platformSettingsService != null) {
                Properties props = platformSettingsService.getBundleProperties(getSymbolicName(), filename);
                config.put(filename, props);
            }

            Properties props = config.get(filename);
            if (props != null) {
                result = props.getProperty(key);
            }
        } catch (IOException e) {
            throw new MotechException("Can't read settings", e);
        }

        return result;
    }

    public Properties getProperties(String filename) {
        if (platformSettingsService != null) {
            try {
                Properties props = platformSettingsService.getBundleProperties(getSymbolicName(), filename);
                config.put(filename, props);
            } catch (IOException e) {
                throw new MotechException("Can't read settings", e);
            }
        }

        Properties result = config.get(filename);
        return (result == null ? new Properties() : result);
    }

    public void setProperty(String filename, String key, String value) {
        if (config.containsKey(filename)) {
            config.put(filename, new Properties());
        }

        Properties props = config.get(filename);

        props.put(key, value);

        if (platformSettingsService != null) {
            try {
                platformSettingsService.saveBundleProperties(getSymbolicName(), filename, props);
            } catch (IOException e) {
                throw new MotechException("Can't save settings " + filename, e);
            }
        }
    }

    public void setProperty(String key, String value) {
        String filename = findFilename(key);

        if (filename == null) {
            throw new MotechException("No file containing key " + key);
        }

        setProperty(filename, key, value);
    }

    public void addConfigProperties(String filename, Properties properties) {
        config.put(filename, properties);

        if (platformSettingsService != null) {
            try {
                platformSettingsService.saveBundleProperties(getSymbolicName(), filename, properties);
            } catch (IOException e) {
                throw new RuntimeException("Can't save settings " + filename, e);
            }
        }
    }

    public void registerProperties(Resource resource) {
        String filename = getResourceFileName(resource);
        InputStream is = null;
        try {
            is = resource.getInputStream();

            Properties props =  new Properties();
            props.load(is);

            config.put(filename, props);
            registerProperties(filename, props);
        } catch (IOException e) {
            throw new MotechException("Error registering resource " + resource.getFilename(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    protected void registerAllProperties() {
        if (platformSettingsService != null) {
            for (Map.Entry<String, Properties> entry : config.entrySet()) {
                String filename = entry.getKey();
                Properties props = entry.getValue();

                registerProperties(filename, props);
            }
        }
    }

    protected void registerProperties(String filename, Properties properties) {
        try {
            Properties registeredProps = platformSettingsService.getBundleProperties(getSymbolicName(), filename);

            if (registeredProps == null) {
                // register new props
                platformSettingsService.saveBundleProperties(getSymbolicName(), filename, properties);
            } else {
                // use registred props
                config.put(filename, registeredProps);
            }
        } catch (IOException e) {
            throw new MotechException("Cant register settings", e);
        }
    }

    protected String getSymbolicName() {
        return bundleContext.getBundle().getSymbolicName();
    }

    protected String findFilename(String key) {
        String result = null;
        for (Map.Entry<String, Properties> entry : config.entrySet()) {
            Properties props = entry.getValue();
            String filename = entry.getKey();

            if (props.containsKey(key)) {
                result = filename;
                break;
            }
        }
        return result;
    }

    protected static String getResourceFileName(Resource resource) {
        return resource.getFilename().replace("classpath:", "");
    }
}
