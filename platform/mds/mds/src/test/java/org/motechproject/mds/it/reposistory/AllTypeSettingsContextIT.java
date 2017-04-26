package org.motechproject.mds.it.reposistory;

import org.junit.Test;
import org.motechproject.mds.it.BaseIT;
import org.motechproject.mds.domain.TypeSetting;
import org.motechproject.mds.domain.TypeSettingOption;
import org.motechproject.mds.repository.internal.AllTypeSettings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.mds.util.Constants.Util.FALSE;
import static org.motechproject.mds.util.Constants.Util.TRUE;

public class AllTypeSettingsContextIT extends BaseIT {
    private static final List EMPTY = Collections.EMPTY_LIST;

    @Autowired
    private AllTypeSettings allTypeSettings;

    @Test
    public void shouldReturnDefaultData() throws Exception {
        List<TypeSetting> settings = allTypeSettings.retrieveAll();

        assertNotNull(settings);
        assertEquals(settings.size(), 14);

        assertTypeSetting(settings, "mds.form.label.precision", asList("REQUIRE", "POSITIVE"), Integer.class, "9");
        assertTypeSetting(settings, "mds.form.label.scale", asList("REQUIRE", "POSITIVE"), Integer.class, "2");
        assertTypeSetting(settings, "mds.form.label.values", asList("REQUIRE"), Collection.class, "[]");
        assertTypeSetting(settings, "mds.form.label.allowUserSupplied", EMPTY, Boolean.class, FALSE);
        assertTypeSetting(settings, "mds.form.label.allowMultipleSelections", EMPTY, Boolean.class, FALSE);
        assertTypeSetting(settings, "mds.form.label.cascadePersist", EMPTY, Boolean.class, TRUE);
        assertTypeSetting(settings, "mds.form.label.cascadeUpdate", EMPTY, Boolean.class, TRUE);
        assertTypeSetting(settings, "mds.form.label.cascadeDelete", EMPTY, Boolean.class, FALSE);
        assertTypeSetting(settings, "mds.form.label.textarea", EMPTY, Boolean.class, FALSE);
        assertTypeSetting(settings, "mds.form.label.maxTextLength", asList("REQUIRE", "POSITIVE"), Integer.class, "255");
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
