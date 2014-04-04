package org.motechproject.commons.api.osgi;

import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.testing.osgi.BasePaxIT;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CommonsApiBundleIT extends BasePaxIT {

    @Test
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
}
