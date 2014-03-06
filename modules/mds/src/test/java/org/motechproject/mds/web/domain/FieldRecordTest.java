package org.motechproject.mds.web.domain;

import org.junit.Test;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.testutil.FieldTestHelper;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class FieldRecordTest {

    @Test
    public void shouldExtendOptions() {
        FieldDto fieldDto = FieldTestHelper.fieldDto(1L, "name", List.class.getName(), "disp", null);
        fieldDto.setSettings(asList(new SettingDto(FieldRecord.FORM_VALUES, asList("one", "two", "three"), null, null)));

        FieldRecord fieldRecord = new FieldRecord(fieldDto);
        fieldRecord.setValue(asList("defVal", "secondVal", "two"));
        assertEquals(asList("one", "two", "three", "defVal", "secondVal"),
                fieldRecord.getSettingByName(FieldRecord.FORM_VALUES).getValue());

        fieldRecord = new FieldRecord(fieldDto);
        fieldRecord.setValue("testSingleObject");
        assertEquals(asList("one", "two", "three", "testSingleObject"),
                fieldRecord.getSettingByName(FieldRecord.FORM_VALUES).getValue());
    }

}
