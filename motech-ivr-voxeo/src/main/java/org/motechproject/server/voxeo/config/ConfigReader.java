package org.motechproject.server.voxeo.config;
import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.stereotype.Component;

@Component
public class ConfigReader {
    public VoxeoConfig getConfig(String filename) {
        return (VoxeoConfig) new MotechJsonReader().readFromFile(filename, new TypeToken<VoxeoConfig>() {
        }.getType());
    }
}