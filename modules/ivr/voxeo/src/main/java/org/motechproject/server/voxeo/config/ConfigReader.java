package org.motechproject.server.voxeo.config;
import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * JSON based Configuration file reader.
 */
@Component
public class ConfigReader {

    private SettingsFacade settings;

    @Autowired
    public void setSettings(SettingsFacade settings) {
        this.settings = settings;
    }

    /**
     * Read json file and return VoxeoConfig. Typically server url and application tokens
     * @param filename json file to read
     * @return VoxeoConfig object with configuration from json file.
     */
    public VoxeoConfig getConfig(String filename) {
        InputStream stream = settings.getRawConfig(filename);

        if (stream == null) {
            throw new MotechException("File not found in classpath: " + filename);
        }

        return (VoxeoConfig) new MotechJsonReader().readFromStream(stream, new TypeToken<VoxeoConfig>() { }.getType());
    }
}
