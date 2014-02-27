package org.motechproject.mds.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeSetting;
import org.motechproject.mds.domain.TypeValidation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AllTypesIT extends BaseIT {

    @Autowired
    private AllTypes allTypes;

    @Test
    public void shouldReturnDefaultData() throws Exception {
        List<Type> types = allTypes.retrieveAll();

        assertNotNull(types);
        assertEquals(types.size(), 9);

        assertType(
                "mds.field.integer", "mds.field.description.integer", Integer.class.getName(),
                null, asList("mds.field.validation.minValue", "mds.field.validation.maxValue", "mds.field.validation.mustBeInSet", "mds.field.validation.cannotBeInSet")
        );
        assertType(
                "mds.field.long", "mds.field.description.long", Long.class.getName(),
                null, null
        );
        assertType(
                "mds.field.string", "mds.field.description.string", String.class.getName(),
                null, asList("mds.field.validation.regex", "mds.field.validation.minLength", "mds.field.validation.maxLength")
        );
        assertType(
                "mds.field.boolean", "mds.field.description.boolean", Boolean.class.getName(),
                null, null
        );
        assertType(
                "mds.field.date", "mds.field.description.date", Date.class.getName(),
                null, null
        );
        assertType(
                "mds.field.time", "mds.field.description.time", Time.class.getName(),
                null, null
        );
        assertType(
                "mds.field.datetime", "mds.field.description.datetime", DateTime.class.getName(),
                null, null
        );
        assertType(
                "mds.field.decimal", "mds.field.description.decimal", Double.class.getName(),
                asList("mds.form.label.precision", "mds.form.label.scale"), asList("mds.field.validation.minValue", "mds.field.validation.maxValue", "mds.field.validation.mustBeInSet", "mds.field.validation.cannotBeInSet")
        );
        assertType(
                "mds.field.combobox", "mds.field.description.combobox", List.class.getName(),
                asList("mds.form.label.values", "mds.form.label.allowUserSupplied", "mds.form.label.allowMultipleSelections"), null
        );
    }

    private void assertType(String displayName, String description, String className,
                            List<String> settings, List<String> validations) {
        Type type = allTypes.retrieveByClassName(className);

        assertNotNull("Not found type with class name: " + className, type);
        assertNotNull("Type should have id", type.getId());
        assertEquals(displayName, type.getDisplayName());
        assertEquals(description, type.getDescription());
        assertEquals(className, type.getTypeClassName());

        assertEquals("Field " + displayName + " settings: ", null != settings, type.hasSettings());
        assertEquals("Field " + displayName + " validations: ", null != validations, type.hasValidation());

        if (null != settings) {
            List<TypeSetting> typeSettings = type.getSettings();
            List<String> strings = new ArrayList<>();

            for (TypeSetting typeSetting : typeSettings) {
                strings.add(typeSetting.getName());
            }

            Collections.sort(settings);
            Collections.sort(strings);

            assertEquals(settings, strings);
        }

        if (null != validations) {
            List<TypeValidation> typeValidations = type.getValidations();
            List<String> strings = new ArrayList<>();

            for (TypeValidation typeValidation : typeValidations) {
                strings.add(typeValidation.getDisplayName());
            }

            Collections.sort(validations);
            Collections.sort(strings);

            assertEquals(validations, strings);
        }
    }

}
