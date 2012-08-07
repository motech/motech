package org.motechproject.server.voxeo.config;
import com.google.gson.reflect.TypeToken;
import org.motechproject.MotechException;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * JSON based Configuration file reader.
 */
@Component
public class ConfigReader {
    /**
     * Read json file and return VoxeoConfig. Typically server url and application tokens
     * @param filename json file to read
     * @return VoxeoConfig object with configuration from json file.
     */
    public VoxeoConfig getConfig(String filename) {
        InputStream stream = getClass().getResourceAsStream(filename);

        if (stream == null) {
            throw new MotechException("File not found in classpath: " + filename);
        }

        return (VoxeoConfig) new MotechJsonReader().readFromStream(stream, new TypeToken<VoxeoConfig>() { }.getType());
    }
}
