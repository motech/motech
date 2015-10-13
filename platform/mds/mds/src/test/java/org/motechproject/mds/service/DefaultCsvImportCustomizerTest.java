package org.motechproject.mds.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.domain.Field;

import java.util.ArrayList;
import java.util.List;

public class DefaultCsvImportCustomizerTest {

    private DefaultCsvImportCustomizer importCustomizer = new DefaultCsvImportCustomizer();

    private Field field1;
    private Field field2;
    private Field field3;
    private Field field4;
    private List<Field> fields;

    @Before
    public void setUp() {
        fields = new ArrayList<>();
        field1 = new Field(null, "name", "Display Name");
        field2 = new Field(null, "name2", "Display Name 2");
        field3 = new Field(null, "name3", "Display Name 3");
        field4 = new Field(null, "DisplayName", "Display Name 4");

        fields.add(field1);
        fields.add(field2);
        fields.add(field3);
        fields.add(field4);
    }

    @Test
    public void shouldFindFieldsByDisplayNames() {
        Assert.assertEquals(field1, importCustomizer.findField("Display Name", fields));
        Assert.assertEquals(field2, importCustomizer.findField("Display Name 2", fields));
        Assert.assertEquals(field3, importCustomizer.findField("Display Name 3", fields));
        Assert.assertEquals(field4, importCustomizer.findField("Display Name 4", fields));
    }

    @Test
    public void shouldFindFieldsByNames() {
        Assert.assertEquals(field1, importCustomizer.findField("name", fields));
        Assert.assertEquals(field2, importCustomizer.findField("name2", fields));
        Assert.assertEquals(field3, importCustomizer.findField("name3", fields));
    }

    @Test
    public void shouldReturnNullWhenFieldDoesnyExist() {
        Assert.assertNull(importCustomizer.findField("name5", fields));
    }
}
