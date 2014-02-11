package org.motechproject.mds.repository;

import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.domain.TypeSetting;
import org.motechproject.mds.domain.TypeSettingOption;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.mds.util.Constants.Util.FALSE;

public class AllTypeSettingsIT extends BaseIT {
    private static final List EMPTY = Collections.EMPTY_LIST;

    @Autowired
    private AllTypeSettings allTypeSettings;

    @Test
    public void shouldReturnDefaultData() throws Exception {
        List<TypeSetting> settings = allTypeSettings.retrieveAll();

        assertNotNull(settings);
        assertEquals(settings.size(), 5);

        assertTypeSetting(settings, "mds.form.label.precision", asList("REQUIRE", "POSITIVE"), Integer.class, "9");
        assertTypeSetting(settings, "mds.form.label.scale", asList("REQUIRE", "POSITIVE"), Integer.class, "2");
        assertTypeSetting(settings, "mds.form.label.values", asList("REQUIRE"), List.class, "[]");
        assertTypeSetting(settings, "mds.form.label.allowUserSupplied", EMPTY, Boolean.class, FALSE);
        assertTypeSetting(settings, "mds.form.label.allowMultipleSelections", EMPTY, Boolean.class, FALSE);
    }

    private void assertTypeSetting(List<TypeSetting> typeSettings, String name,
                                   List<String> typeSettingOptions, Class<?> typeClass,
                                   String defaultValue) {
        TypeSetting typeSetting = retrieveByName(typeSettings, name);

        assertNotNull("Not found type setting with name: " + name, typeSetting);
        assertNotNull("Type setting should have id", typeSetting.getId());
        assertEquals(name, typeSetting.getName());

        List<TypeSettingOption> options = typeSetting.getTypeSettingOptions();
        List<String> optionsNames = new ArrayList<>();
        for (TypeSettingOption option : options) {
            optionsNames.add(option.getName());
        }

        assertEquals(typeSettingOptions, optionsNames);

        assertEquals(typeClass, typeSetting.getValueType().getTypeClass());
        assertEquals(defaultValue, typeSetting.getDefaultValue());
    }

    private TypeSetting retrieveByName(List<TypeSetting> typeSettings, String defaultName) {
        TypeSetting found = null;

        for (TypeSetting typeSetting : typeSettings) {
            if (typeSetting.getName().equalsIgnoreCase(defaultName)) {
                found = typeSetting;
                break;
            }
        }

        return found;
    }

}
