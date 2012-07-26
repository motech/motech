package org.motechproject.eventlogging.repository;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.eventlogging.domain.MappingsJson;
import org.springframework.stereotype.Component;
import com.google.gson.reflect.TypeToken;

@Component
public class AllEventMappings {

    private static final String MAPPING_FILE_NAME = "eventmappings.json";

    private MotechJsonReader motechJsonReader;

    public AllEventMappings() {
        this.motechJsonReader = new MotechJsonReader();
    }

    public List<MappingsJson> getAllMappings() {
        Type type = new TypeToken<List<MappingsJson>>() {
        }.getType();

        InputStream is = getClass().getClassLoader().getResourceAsStream(MAPPING_FILE_NAME);

        List<MappingsJson> mappings = (List<MappingsJson>) motechJsonReader.readFromStream(is, type);

        return mappings;
    }

}
