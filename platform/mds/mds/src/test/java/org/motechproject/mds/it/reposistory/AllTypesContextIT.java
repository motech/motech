package org.motechproject.mds.it.reposistory;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.it.BaseIT;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.domain.Relationship;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeSetting;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.repository.internal.AllTypes;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AllTypesContextIT extends BaseIT {
    private static final int START_NUMBER_OF_TYPES = 25;

    @Autowired
    private AllTypes allTypes;

    @Test
    public void shouldReturnDefaultData() throws Exception {
        List<Type> types = allTypes.retrieveAll();

        assertNotNull(types);
        assertEquals(START_NUMBER_OF_TYPES, types.size());

        assertType(
                "mds.field.integer", "mds.field.description.integer", Integer.class.getName(),
                null, asList("mds.field.validation.minValue", "mds.field.validation.maxValue", "mds.field.validation.mustBeInSet", "mds.field.validation.cannotBeInSet")
        );
        assertType(
                "mds.field.string", "mds.field.description.string", String.class.getName(),
                asList("mds.form.label.textarea", "mds.form.label.maxTextLength"),
                asList("mds.field.validation.regex", "mds.field.validation.minLength", "mds.field.validation.maxLength")
        );
        assertType(
                "mds.field.boolean", "mds.field.description.boolean", Boolean.class.getName(),
                null, null
        );
        assertType(
                "mds.field.javaUtilDate", "mds.field.description.date", Date.class.getName(),
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
                "mds.field.datetime8", "mds.field.description.datetime", LocalDateTime.class.getName(),
                null, null
        );
        assertType(
                "mds.field.decimal", "mds.field.description.decimal", Double.class.getName(),
                asList("mds.form.label.precision", "mds.form.label.scale"), asList("mds.field.validation.minValue", "mds.field.validation.maxValue", "mds.field.validation.mustBeInSet", "mds.field.validation.cannotBeInSet")
        );
        assertType(
                "mds.field.combobox", "mds.field.description.combobox", Collection.class.getName(),
                asList("mds.form.label.values", "mds.form.label.allowUserSupplied", "mds.form.label.allowMultipleSelections"), null
        );
        assertType(
                "mds.field.map", "mds.field.description.map", Map.class.getName(),
                null, null
        );
        assertType(
                "mds.field.locale", "mds.field.description.locale", Locale.class.getName(),
                null, null
        );
        assertType(
                "mds.field.blob", "mds.field.description.blob", Byte[].class.getName(),
                null, null
        );
        assertType(
                "mds.field.long", "mds.field.description.long", Long.class.getName(),
                null, null
        );
        assertType(
                "mds.field.date", "mds.field.description.localDate", LocalDate.class.getName(),
                null, null
        );
        assertType(
                "mds.field.date8", "mds.field.description.localDate", java.time.LocalDate.class.getName(),
                null, null
        );
        assertType(
                "mds.field.relationship", "mds.field.description.relationship", Relationship.class.getName(),
                asList("mds.form.label.cascadePersist", "mds.form.label.cascadeUpdate", "mds.form.label.cascadeDelete"), null
        );
        assertType(
                "mds.field.relationship.oneToMany", "mds.field.description.relationship.oneToMany", OneToManyRelationship.class.getName(),
                asList("mds.form.label.cascadePersist", "mds.form.label.cascadeUpdate", "mds.form.label.cascadeDelete", "mds.form.label.showCount", "mds.form.label.expandByDefault", "mds.form.label.allowAddingExisting", "mds.form.label.allowAddingNew"), null
        );
        assertType(
                "mds.field.relationship.oneToOne", "mds.field.description.relationship.oneToOne", OneToOneRelationship.class.getName(),
                asList("mds.form.label.cascadePersist", "mds.form.label.cascadeUpdate", "mds.form.label.cascadeDelete", "mds.form.label.expandByDefault", "mds.form.label.allowAddingExisting", "mds.form.label.allowAddingNew"), null
        );
        assertType(
        	"mds.field.float", "mds.field.description.float", Float.class.getName(),
                asList("mds.form.label.precision", "mds.form.label.scale"), asList("mds.field.validation.minValue", "mds.field.validation.maxValue", "mds.field.validation.mustBeInSet", "mds.field.validation.cannotBeInSet")
        );
        assertType(
        	"mds.field.short", "mds.field.description.short", Short.class.getName(),
                null, asList("mds.field.validation.minValue", "mds.field.validation.maxValue", "mds.field.validation.mustBeInSet", "mds.field.validation.cannotBeInSet")
        );
        assertType(
        	"mds.field.character", "mds.field.description.character", Character.class.getName(),
                null, null
        );
        assertType(
                "mds.field.uuid", "mds.field.description.uuid", UUID.class.getName(), null, null
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
