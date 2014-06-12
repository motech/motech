package org.motechproject.mds.query;

import org.junit.Test;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeSetting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class PropertyBuilderTest {

    @Test
    public void shouldCreatePropertyByFieldAndValue() throws Exception {
        Type type = new Type(String.class);
        Field field = new Field(null, "ala", "ala",type, true, false);

        assertProperty(PropertyBuilder.create(field, "cat"), "ala", "cat");
    }

    @Test
    public void shouldCreatePropertyByNameAndValue() throws Exception {
        assertProperty(PropertyBuilder.create("test", 100L), "test", 100L);
    }

    @Test
    public void shouldGenerateAppropriatePropertyType() throws Exception {
        Set<String> set = new HashSet<>();
        Range<Integer> range = new Range<>(0, 1);

        Type type = new Type("mds.field.combobox", "", List.class);
        Entity entity = new Entity("org.motechproject.mds.Sample");
        Field field = new Field(entity, "roles","roles", type, true, false);

        // should not create collection property for fields without list field
        assertProperty(
                PropertyBuilder.create("roles", 1L),
                EqualProperty.class, "roles", 1L
        );

        field.addSetting(new FieldSetting(field, new TypeSetting("mds.form.label.allowMultipleSelections"), "true"));

        // should create collection property for enum list
        assertProperty(
                PropertyBuilder.create(field, "role"),
                CollectionProperty.class, "roles", Arrays.asList("role")
        );

        field.addSetting(new FieldSetting(field, new TypeSetting("mds.form.label.allowUserSupplied"), "true"));

        // should create collection property for string list
        assertProperty(
                PropertyBuilder.create(field, "role"),
                CollectionProperty.class, "roles", Arrays.asList("role")
        );

        assertProperty(
                PropertyBuilder.create("set", set),
                SetProperty.class, "set", set
        );
        assertProperty(
                PropertyBuilder.create("range", range),
                RangeProperty.class, "range", range
        );
        assertProperty(
                PropertyBuilder.create("equal", 1L),
                EqualProperty.class, "equal", 1L
        );
    }

    private void assertProperty(Property property, Class clazz, String name, Object value) {
        assertEquals(clazz, property.getClass());
        assertProperty(property, name, value);
    }

    private void assertProperty(Property property, String name, Object value) {
        assertEquals(name, property.getName());
        assertEquals(value, property.getValue());
    }
}
