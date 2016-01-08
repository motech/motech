package org.motechproject.mds.helper;

import org.junit.Test;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.ValidationCriterionDto;
import org.motechproject.mds.testutil.FieldTestHelper;
import org.motechproject.mds.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FieldHelperTest {

    @Test
    public void shouldSetCorrectValuesForFields() {
        FieldDto field = fieldDto();

        FieldHelper.setField(field, "basic.displayName", asList("newDispName"));
        assertEquals("newDispName", field.getBasic().getDisplayName());
        FieldHelper.setField(field, "basic.tooltip", asList("newTooltip"));
        assertEquals("newTooltip", field.getBasic().getTooltip());
        FieldHelper.setField(field, "basic.placeholder", asList("newPlaceholder"));
        assertEquals("newPlaceholder", field.getBasic().getPlaceholder());

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
    public void shouldAddMetadataForFields() {
        Entity entity = new Entity("SampleEntity");
        Field field = new Field(entity, "sampleField", "Display Name", true, false, false, false, false, "default", "tooltip", "placeholder", new HashSet<Lookup>());
        FieldHelper.addMetadataForRelationship(TypeDto.MANY_TO_MANY_RELATIONSHIP.getTypeClass(), field);
        assertEquals(field.getMetadata().size(), 4);
        field.getMetadata().clear();
        FieldHelper.addMetadataForRelationship(TypeDto.ONE_TO_MANY_RELATIONSHIP.getTypeClass(), field);
        assertEquals(field.getMetadata().size(), 2);
        field.getMetadata().clear();
        FieldHelper.addMetadataForRelationship(TypeDto.ONE_TO_ONE_RELATIONSHIP.getTypeClass(), field);
        assertEquals(field.getMetadata().size(), 1);
    }

    @Test
    public void shouldCreateAndSetMetadataForManyToManyRelationship() {
        Entity entity = new Entity("SampleEntity");
        Field field = new Field(entity, "sampleField", "Display Name", true, false, false, false, false, "default", "tooltip", "placeholder", new HashSet<Lookup>());
        FieldHelper.createMetadataForManyToManyRelationship(field, "org.motechproject.sample.Test", "java.util.Set", "relatedField", true);
        assertEquals(field.getMetadata().size(), 4);
        assertEquals(field.getMetadataValue(Constants.MetadataKeys.OWNING_SIDE), "true");
        assertEquals(field.getMetadataValue(Constants.MetadataKeys.RELATED_FIELD), "relatedField");
        assertEquals(field.getMetadataValue(Constants.MetadataKeys.RELATED_CLASS), "org.motechproject.sample.Test");
        assertEquals(field.getMetadataValue(Constants.MetadataKeys.RELATIONSHIP_COLLECTION_TYPE), "java.util.Set");
    }

    @Test
    public void shouldAddAndUpdateComboboxMetadata() {
        Entity entity = new Entity("SampleEntity");
        entity.setClassName("org.motechproject.samplemodule.domain.SampleEntity");
        Field field = FieldTestHelper.fieldWithComboboxSettings(entity, "sampleField", "Display Name", String.class, false, false, asList("Item_1", "Item_2"));

        FieldHelper.addOrUpdateMetadataForCombobox(field);
        assertEquals("org.motechproject.samplemodule.domain.mdsenum.SampleEntitySampleField", field.getMetadata(Constants.MetadataKeys.ENUM_CLASS_NAME).getValue());

        Entity otherEntity = new Entity("OtherEntity");
        otherEntity.setClassName("org.motechproject.samplemodule.domain.OtherEntity");
        field.setEntity(otherEntity);
        FieldHelper.addOrUpdateMetadataForCombobox(field);
        assertEquals("org.motechproject.samplemodule.domain.mdsenum.OtherEntitySampleField", field.getMetadata(Constants.MetadataKeys.ENUM_CLASS_NAME).getValue());
    }

    private FieldDto fieldDto() {
        TypeDto type = new TypeDto("typeDispName", "typeDesc", "typeDefaultName", "typeClass");
        FieldBasicDto basic = new FieldBasicDto("fieldDispName", "fieldName", true, false, "defVal", "tooltip", "placeholder");
        MetadataDto metadata = new MetadataDto("key", "val");
        ValidationCriterionDto criterion = new ValidationCriterionDto("criterionDispName", type);
        FieldValidationDto validation = new FieldValidationDto(criterion);
        return new FieldDto(1L, 100L, type, basic, false, new ArrayList<>(asList(metadata)), validation, null, null);
    }

    private AdvancedSettingsDto advancedSettingsDto() {
        AdvancedSettingsDto advancedSettings = new AdvancedSettingsDto();
        advancedSettings.setEntityId(100L);

        LookupDto lookup = new LookupDto(1L, "look1Name", true, true, null, false, "look1", new ArrayList<String>());
        LookupDto lookup2 = new LookupDto(2L, "look2Name", false, false, null, false, "look2", new ArrayList<String>());
        advancedSettings.setIndexes(new ArrayList<>((asList(lookup, lookup2))));

        return advancedSettings;
    }
}

