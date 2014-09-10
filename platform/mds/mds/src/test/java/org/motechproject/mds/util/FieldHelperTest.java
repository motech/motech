package org.motechproject.mds.util;

import org.junit.Test;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.ValidationCriterionDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FieldHelperTest {

    @Test
    public void shouldSetCorrectValuesForFields() {
        FieldDto field = fieldDto();

        FieldHelper.setField(field, "basic.displayName", asList("newDispName"));
        assertEquals("newDispName", field.getBasic().getDisplayName());
        FieldHelper.setField(field, "basic.tooltip", asList("newTooltip"));
        assertEquals("newTooltip", field.getBasic().getTooltip());

        FieldHelper.setField(field, "metadata.0.key", asList("newKey"));
        assertEquals("newKey", field.getMetadata().get(0).getKey());
    }

    @Test
    public void shouldAddAndRemoveLookups() {
        AdvancedSettingsDto advancedSettings = advancedSettingsDto();

        FieldHelper.setField(advancedSettings, "$addNewIndex", asList("Lookup 1"));
        assertEquals(3, advancedSettings.getIndexes().size());

        FieldHelper.setField(advancedSettings, "$removeIndex", asList(1));
        assertEquals(2, advancedSettings.getIndexes().size());
    }

    @Test
    public void shouldEditLookups() {
        AdvancedSettingsDto advancedSettingsDto = advancedSettingsDto();

        FieldHelper.setField(advancedSettingsDto, "indexes.0.lookupName", asList("newVal"));
        assertEquals("newVal", advancedSettingsDto.getIndexes().get(0).getLookupName());

        FieldHelper.setField(advancedSettingsDto, "indexes.1.singleObjectReturn", asList(true));
        assertTrue(advancedSettingsDto.getIndexes().get(1).isSingleObjectReturn());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForWrongPaths() {
        FieldHelper.setField(fieldDto(), "wrong.wrong.wrong", asList("val"));
    }

    @Test
    public void shouldReturnFieldListAsMapById() {
        List<FieldDto> fields = simpleFieldsList();

        Map<Long, FieldDto> fieldMap = FieldHelper.asFieldMapById(fields);

        for (long i = 4; i < 9; i++) {
            assertMapEntry(fieldMap, i, "name" + i, i);
        }
    }

    @Test
    public void shouldReturnFieldListAsMapByName() {
        List<FieldDto> fields = simpleFieldsList();

        Map<String, FieldDto> fieldMap = FieldHelper.asFieldMapByName(fields);

        for (long i = 4; i < 9; i++) {
            String name = "name" + i;
            assertMapEntry(fieldMap, name, name, i);
        }
    }

    private FieldDto fieldDto() {
        TypeDto type = new TypeDto("typeDispName", "typeDesc", "typeDefaultName", "typeClass");
        FieldBasicDto basic = new FieldBasicDto("fieldDispName", "fieldName", true, "defVal", "tooltip");
        MetadataDto metadata = new MetadataDto("key", "val");
        ValidationCriterionDto criterion = new ValidationCriterionDto("criterionDispName", type);
        FieldValidationDto validation = new FieldValidationDto(criterion);
        return new FieldDto(1L, 100L, type, basic, false, new ArrayList<>(asList(metadata)), validation, null, null);
    }

    private AdvancedSettingsDto advancedSettingsDto() {
        AdvancedSettingsDto advancedSettings = new AdvancedSettingsDto();
        advancedSettings.setEntityId(100L);

        LookupDto lookup = new LookupDto(1L, "look1Name", true, true, null, false, "look1");
        LookupDto lookup2 = new LookupDto(2L, "look2Name", false, false, null, false, "look2");
        advancedSettings.setIndexes(new ArrayList<>((asList(lookup, lookup2))));

        return advancedSettings;
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

