package org.motechproject.mds.web.domain;

import org.junit.Test;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.web.FieldTestHelper;
import org.motechproject.mds.util.Constants;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FieldRecordTest {

    @Test
    public void shouldExtendOptionsAndHandleDefListValuesForMultiSelect() {
        FieldDto fieldDto = FieldTestHelper.fieldDto(1L, "name", List.class.getName(), "disp", "[one, two]");
        fieldDto.setSettings(asList(new SettingDto(Constants.Settings.COMBOBOX_VALUES, asList("one", "two", "three"), null, null),
                new SettingDto(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS, true, null, null)));

        FieldRecord fieldRecord = new FieldRecord(fieldDto);
        assertEquals(asList("one", "two"), fieldRecord.getValue());

        fieldRecord.setValue(asList("defVal", "secondVal", "two"));
        assertEquals(asList("one", "two", "three", "defVal", "secondVal"),
                fieldRecord.getSettingByName(Constants.Settings.COMBOBOX_VALUES).getValue());

        fieldRecord = new FieldRecord(fieldDto);
        fieldRecord.setValue("testSingleObject");
        assertEquals(asList("one", "two", "three", "testSingleObject"),
                fieldRecord.getSettingByName(Constants.Settings.COMBOBOX_VALUES).getValue());

        fieldRecord.setValue("[one, two]");
        assertEquals(asList("one", "two"), fieldRecord.getValue());
        assertEquals(asList("one", "two", "three", "testSingleObject"),
                fieldRecord.getSettingByName(Constants.Settings.COMBOBOX_VALUES).getValue());

        fieldRecord.setValue("[one, two]");
        assertEquals(asList("one", "two"), fieldRecord.getValue());

        // should return a string for single selections
        fieldRecord.setValue("defVal");
        assertEquals(asList("defVal"), fieldRecord.getValue());

        fieldRecord.setValue("[defVal]");
        assertEquals(asList("defVal"), fieldRecord.getValue());

        // test with enum
        fieldRecord.setValue(TestEnum.ONE);
        assertEquals(asList("ONE"), fieldRecord.getValue());

        fieldRecord.setValue(asList(TestEnum.ONE, TestEnum.THREE));
        assertEquals(asList("ONE", "THREE"), fieldRecord.getValue());

        fieldRecord.setValue(null);
        assertNull(fieldRecord.getValue());
    }

    @Test
    public void shouldHandleSingleSelectComboBoxes() {
        FieldDto fieldDto = FieldTestHelper.fieldDto(1L, "name", List.class.getName(), "disp", "[one, two]");
        fieldDto.setSettings(asList(new SettingDto(Constants.Settings.COMBOBOX_VALUES, asList("one", "two", "three"), null, null)));

        FieldRecord fieldRecord = new FieldRecord(fieldDto);

        fieldRecord.setValue("[two]");
        assertEquals("two", fieldRecord.getValue());

        fieldRecord.setValue("test");
        assertEquals("test", fieldRecord.getValue());

        // test with enum
        fieldRecord.setValue(TestEnum.TWO);
        assertEquals("TWO", fieldRecord.getValue());

        fieldRecord.setValue(asList(TestEnum.ONE));
        assertEquals("ONE", fieldRecord.getValue());

        fieldRecord.setValue(null);
        assertNull(fieldRecord.getValue());
    }

    public enum TestEnum {
        ONE, TWO, THREE
    }
}
