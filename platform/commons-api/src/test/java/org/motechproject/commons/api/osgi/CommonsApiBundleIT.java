package org.motechproject.commons.api.osgi;

import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.testing.osgi.BaseOsgiIT;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class CommonsApiBundleIT extends BaseOsgiIT {

    public void testCommonsApi() {
        String json = "{'name':'testName'}";
        final MotechJsonReader motechJsonReader = new MotechJsonReader();
        Type type = new TypeToken<TestRecord>() {
        }.getType();
        final Object jsonObject = motechJsonReader.readFromStream(new ByteArrayInputStream(json.getBytes()), type);
        assertTrue(jsonObject instanceof TestRecord);
        assertEquals("testName", ((TestRecord) jsonObject).name);
    }

    private class TestRecord {
        String name;
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.commons.api.json",
                "com.google.gson.reflect");
    }
}
