package org.motechproject.mds.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.TypeDto;

import java.util.ArrayList;
import java.util.List;

public class DefaultCsvImportCustomizerTest {

    private DefaultCsvImportCustomizer importCustomizer = new DefaultCsvImportCustomizer();

    private FieldDto field1;
    private FieldDto field2;
    private FieldDto field3;
    private FieldDto field4;
    private List<FieldDto> fields;

    @Before
    public void setUp() {
        fields = new ArrayList<>();
        field1 = new FieldDto("name", "Display Name", TypeDto.STRING);
        field2 = new FieldDto("name2", "Display Name 2", TypeDto.STRING);
        field3 = new FieldDto("name3", "Display Name 3", TypeDto.STRING);
        field4 = new FieldDto("DisplayName", "Display Name 4", TypeDto.STRING);
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
