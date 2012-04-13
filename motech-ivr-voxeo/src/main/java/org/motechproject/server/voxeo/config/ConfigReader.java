package org.motechproject.server.voxeo.config;
import com.google.gson.reflect.TypeToken;
import org.motechproject.MotechException;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class ConfigReader {
    public VoxeoConfig getConfig(String filename) {
        InputStream stream = getClass().getResourceAsStream(filename);

        if (stream == null) {
            throw new MotechException("File not found in classpath: " + filename);
        }

        return (VoxeoConfig) new MotechJsonReader().readFromStream(stream, new TypeToken<VoxeoConfig>() {
        }.getType());
    }
}