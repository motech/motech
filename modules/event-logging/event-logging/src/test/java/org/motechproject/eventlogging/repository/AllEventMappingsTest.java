package org.motechproject.eventlogging.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.eventlogging.domain.MappingsJson;
import org.motechproject.eventlogging.domain.ParametersPresentEventFlag;
import org.motechproject.server.config.SettingsFacade;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllEventMappingsTest {

    @Mock
    private SettingsFacade settings;

    @Before
    public void setup() {
        initMocks(this);
        when(settings.getRawConfig(AllEventMappings.MAPPING_FILE_NAME)).
            thenReturn(getClass().getClassLoader().getResourceAsStream(AllEventMappings.MAPPING_FILE_NAME));
    }

    @Test
    public void shouldReadTheEventMappingsFileCorrectly() {

        AllEventMappings allEventMappings = new AllEventMappings(settings);

        List<MappingsJson> allMappings = allEventMappings.getAllMappings();

        MappingsJson firstMapping = allMappings.get(0);

        assertNotNull(firstMapping);

        List<ParametersPresentEventFlag> flags = firstMapping.getFlags();

        assertEquals(flags.size(), 2);

        ParametersPresentEventFlag firstFlag = flags.get(0);

        Map<String, String> flagParameters = firstFlag
                .getKeyValuePairsPresent();

        assertNotNull(flagParameters);

        assertEquals(flagParameters.get("requiredParameter1"), "test");
        assertEquals(flagParameters.get("requiredParameter2"), "test2");

        List<Map<String, String>> mappings = firstMapping.getMappings();

        assertEquals(mappings.size(), 2);

        Map<String, String> mapping = mappings.get(0);

        assertEquals(mapping.get("status"), "ok");
        assertEquals(mapping.get("result"), "success");

        Map<String, String> mapping2 = mappings.get(1);

        assertNotNull(mapping2);

        assertEquals(mapping2.get("status"), "error");
        assertEquals(mapping2.get("result"), "failure");

        assertNull(mapping.get("doesNotExist"));

        List<String> excludes = firstMapping.getExcludes();

        assertEquals(excludes.get(0), "exclude1");
        assertEquals(excludes.get(1), "exclude2");

        List<String> includes = firstMapping.getIncludes();

        assertEquals(includes.get(0), "include1");
        assertEquals(includes.get(1), "include2");
    }
}
