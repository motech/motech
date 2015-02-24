package org.motechproject.mds.docs.swagger;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.mds.docs.swagger.model.Property;
import org.motechproject.mds.domain.FieldInfo;
import org.motechproject.mds.testutil.FieldTestHelper;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SwaggerFieldConverterTest {

    @Test
    public void shouldConvertFieldsToProperties() {
        Property property = SwaggerFieldConverter.fieldToProperty(field(DateTime.class));
        verifySimpleProperty(property, "string", "date-time");

        property = SwaggerFieldConverter.fieldToProperty(field(Date.class));
        verifySimpleProperty(property, "string", "date-time");

        property = SwaggerFieldConverter.fieldToProperty(field(LocalDate.class));
        verifySimpleProperty(property, "string", "date");

        property = SwaggerFieldConverter.fieldToProperty(field(String.class));
        verifySimpleProperty(property, "string", null);

        property = SwaggerFieldConverter.fieldToProperty(field(Locale.class));
        verifySimpleProperty(property, "string", null);

        property = SwaggerFieldConverter.fieldToProperty(field(Integer.class));
        verifySimpleProperty(property, "integer", "int32");

        property = SwaggerFieldConverter.fieldToProperty(field(Long.class));
        verifySimpleProperty(property, "integer", "int64");

        property = SwaggerFieldConverter.fieldToProperty(field(Double.class));
        verifySimpleProperty(property, "number", "double");

        property = SwaggerFieldConverter.fieldToProperty(field(Boolean.class));
        verifySimpleProperty(property, "boolean", null);
    }

    @Test
    public void shouldConvertComboboxes() {
        // not user-supplied

        Property property = SwaggerFieldConverter.fieldToProperty(field(List.class));
        verifySimpleProperty(property, "string", null);

        FieldInfo field = field(List.class);
        field.getTypeInfo().setCombobox(true);
        field.getTypeInfo().setAllowsMultipleSelection(true);
        field.getTypeInfo().setItems(asList("a", "b", "c"));
        property = SwaggerFieldConverter.fieldToProperty(field);

        verifyComboboxProperty(property, true);

        // user-supplied

        field.getTypeInfo().setAllowUserSupplied(true);

        property = SwaggerFieldConverter.fieldToProperty(field);

        verifyComboboxProperty(property, false);
    }

    private FieldInfo field(Class type) {
        return FieldTestHelper.fieldInfo("name", type, true, true, false);
    }

    private void verifySimpleProperty(Property property, String expectedType, String expectedFormat) {
        assertNotNull(property);
        assertEquals(expectedType, property.getType());
        assertEquals(expectedFormat, property.getFormat());
        assertNull(property.getItems());
    }

    private void verifyComboboxProperty(Property property, boolean shouldProvideEnumList) {
        assertNotNull(property);
        assertEquals("array", property.getType());
        assertNull(property.getFormat());
        Property items = property.getItems();
        assertNotNull(items);
        assertEquals("string", items.getType());
        assertNull(items.getFormat());

        if (shouldProvideEnumList) {
            assertEquals(asList("a", "b", "c"), items.getEnumValues());
        } else {
            assertNull(items.getEnumValues());
        }
    }
}
