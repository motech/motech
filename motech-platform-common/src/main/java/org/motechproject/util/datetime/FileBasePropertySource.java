package org.motechproject.util.datetime;

import org.motechproject.util.DateUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class FileBasePropertySource implements PropertySource {
    private String file;

    public FileBasePropertySource(String file) {
        this.file = file;
    }

    private Properties properties() {
        Properties properties = new Properties();
        FileInputStream fileInputStream;
        try {
            URL resource = DateUtil.class.getResource(file);
            if (resource == null) return properties;
            String file = resource.getFile();
            fileInputStream = new FileInputStream(file);
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException ignored) {
        }
        return properties;
    }

    @Override
    public String getProperty(String propertyName) {
        Object property = properties().get(propertyName);
        return property == null ? null : property.toString();
    }
}
