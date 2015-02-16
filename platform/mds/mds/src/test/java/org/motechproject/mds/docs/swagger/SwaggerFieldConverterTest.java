package org.motechproject.mds.docs.swagger;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.mds.docs.swagger.model.Property;
import org.motechproject.mds.domain.FieldInfo;

import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        Property property = SwaggerFieldConverter.fieldToProperty(field(List.class));
        verifySimpleProperty(property, "string", null);

        FieldInfo field = field(List.class);
        field.setAdditionalTypeInfo(FieldInfo.TypeInfo.ALLOWS_MULTIPLE_SELECTIONS);
        property = SwaggerFieldConverter.fieldToProperty(field);

        assertNotNull(property);
        assertEquals("array", property.getType());
        assertNull(property.getFormat());
        Property items = property.getItems();
        assertNotNull(items);
        assertEquals("string", items.getType());
        assertNull(items.getFormat());
    }

    private FieldInfo field(Class type) {
        return new FieldInfo("name", "disp", type.getName(), true, true);
    }

    private void verifySimpleProperty(Property property, String expectedType, String expectedFormat) {
        assertNotNull(property);
        assertEquals(expectedType, property.getType());
        assertEquals(expectedFormat, property.getFormat());
        assertNull(property.getItems());
    }
}
