package org.motechproject.config.bootstrap.impl;

import org.motechproject.config.bootstrap.ConfigFileReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Component
public class ConfigFileReaderImpl implements ConfigFileReader {

    @Override
    public Properties getProperties(File file) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        return properties;
    }
}
