package org.motechproject.settings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testPlatformServerApplicationContext.xml"})
public class MotechPlatformSettingsIT {

    @Test
    public void testDefaultLanguage() throws Exception {
        String language = MotechPlatformSettings.getInstance().getDefaultLanguage();
        assertEquals(language, "en");
    }
}