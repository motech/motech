package org.motechproject.server.voxeo.config;
import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.stereotype.Component;

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
        return (VoxeoConfig) new MotechJsonReader().readFromFile(filename, new TypeToken<VoxeoConfig>() {
        }.getType());
    }
}