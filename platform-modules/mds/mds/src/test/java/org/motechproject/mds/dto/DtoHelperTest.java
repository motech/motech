package org.motechproject.mds.dto;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DtoHelperTest {

    @Test
    public void shouldReturnFieldListAsMapById() {
        List<FieldDto> fields = simpleFieldsList();

        Map<Long, FieldDto> fieldMap = DtoHelper.asFieldMapById(fields);

        for (long i = 4; i < 9; i++) {
            assertMapEntry(fieldMap, i, "name" + i, i);
        }
    }

    @Test
    public void shouldReturnFieldListAsMapByName() {
        List<FieldDto> fields = simpleFieldsList();

        Map<String, FieldDto> fieldMap = DtoHelper.asFieldMapByName(fields);

        for (long i = 4; i < 9; i++) {
            String name = "name" + i;
            assertMapEntry(fieldMap, name, name, i);
        }
    }

    private List<FieldDto> simpleFieldsList() {
        List<FieldDto> fields = new ArrayList<>();
        for (long i = 4; i < 9; i++) {
            FieldDto field = new FieldDto();
            field.setBasic(new FieldBasicDto("something", "name" + i));
            field.setId(i);

            fields.add(field);
        }
        return fields;
    }

    private void assertMapEntry(Map map, Object key, String name, Long id) {
        assertNotNull(map.get(key));
        FieldDto field = (FieldDto) map.get(key);
        assertEquals(name, field.getBasic().getName());
        assertEquals(id, field.getId());
    }
}
