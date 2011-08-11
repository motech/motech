package org.motechproject.mobileforms.api.dao;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.mobileforms.api.domain.FormGroup;

import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

public class AllMobileFormsTest {

    private AllMobileForms allMobileForms;

    @Before
    public void setup() {
        Properties properties = new Properties();
        properties.setProperty("forms.config.file", "forms-config.json");
        MotechJsonReader motechJsonReader = new MotechJsonReader();
        allMobileForms = new AllMobileForms(properties, motechJsonReader);
    }

    @Test
    public void getAllFormGroupsTest() {
        List<FormGroup> formGroups = allMobileForms.getAllFormGroups();
        assertEquals(2, formGroups.size());
        assertEquals("GroupName-I", formGroups.get(0).getName());
        assertEquals("GroupName-II", formGroups.get(1).getName());
    }

}
