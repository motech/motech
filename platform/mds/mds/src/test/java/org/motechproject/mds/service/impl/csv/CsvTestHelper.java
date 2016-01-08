package org.motechproject.mds.service.impl.csv;

import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.BrowsingSettingsDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.entityinfo.EntityInfo;
import org.motechproject.mds.testutil.records.Record2;
import org.motechproject.mds.testutil.records.RecordEnum;
import org.motechproject.mds.testutil.records.RelatedClass;
import org.motechproject.mds.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

public class CsvTestHelper {

    public static void mockRecord2Fields(EntityInfo entityInfoMock, AdvancedSettingsDto advancedSettingsDtoMock, BrowsingSettingsDto browsingSettingsDtoMock) {
        List<FieldDto> fieldDtos = new ArrayList<>();

        fieldDtos.add(getFieldWithId(123l, "id", "ID", TypeDto.LONG));
        fieldDtos.add(getFieldWithId(124l, "creator", "Creator", TypeDto.STRING));
        fieldDtos.add(getFieldWithId(125l,"owner", "Owner", TypeDto.STRING));
        fieldDtos.add(getFieldWithId(126l, "modifiedBy", "Modified By", TypeDto.STRING));
        fieldDtos.add(getFieldWithId(127l, "creationDate", "Creation date", TypeDto.DATETIME));
        fieldDtos.add(getFieldWithId(128l, "modificationDate", "Modification date", TypeDto.DATETIME));
        fieldDtos.add(getFieldWithId(129l, "value", "Value Disp", TypeDto.STRING));
        fieldDtos.add(getFieldWithId(130l, "date", "Date disp", TypeDto.DATE));
        fieldDtos.add(getFieldWithId(131l, "dateIgnoredByRest", "dateIgnoredByRest disp", TypeDto.DATE));
        fieldDtos.add(comboboxField(132l, "enumField", false));
        fieldDtos.add(comboboxField(133l, "enumListField", true));
        fieldDtos.add(relationshipField(134l, "singleRelationship", TypeDto.ONE_TO_ONE_RELATIONSHIP));
        fieldDtos.add(relationshipField(135l, "multiRelationship", TypeDto.ONE_TO_MANY_RELATIONSHIP, ArrayList.class));

        when(entityInfoMock.getFieldDtos()).thenReturn(fieldDtos);
        when(entityInfoMock.getClassName()).thenReturn(Record2.class.getName());
        when(entityInfoMock.getAdvancedSettings()).thenReturn(advancedSettingsDtoMock);
        when(advancedSettingsDtoMock.getBrowsing()).thenReturn(browsingSettingsDtoMock);
        when(browsingSettingsDtoMock.getDisplayedFields()).thenReturn(asList(129l, 130l));
    }

    private static FieldDto comboboxField(Long id, String name, boolean isList) {
        FieldDto fieldDto = new FieldDto(name, name + " Disp", TypeDto.COLLECTION);
        fieldDto.setId(id);
        fieldDto.addMetadata(new MetadataDto(Constants.MetadataKeys.ENUM_CLASS_NAME, RecordEnum.class.getName()));
        SettingDto settingDto = new SettingDto(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS, String.valueOf(isList), TypeDto.BOOLEAN);
        fieldDto.addSetting(settingDto);

        return fieldDto;
    }

    private static FieldDto relationshipField(Long id, String name, TypeDto relationshipType) {
        return relationshipField(id, name, relationshipType, null);
    }

    private static FieldDto relationshipField(Long id,  String name, TypeDto relationshipType, Class collectionType) {
        FieldDto fieldDto = new FieldDto(name, name + " Disp", relationshipType);
        fieldDto.setId(id);
        fieldDto.addMetadata(new MetadataDto(Constants.MetadataKeys.RELATED_CLASS, RelatedClass.class.getName()));
        if (collectionType != null) {
            fieldDto.addMetadata(new MetadataDto(Constants.MetadataKeys.RELATIONSHIP_COLLECTION_TYPE, collectionType.getName()));
        }

        return fieldDto;
    }

    private static FieldDto getFieldWithId(Long id, String name, String displayName, TypeDto typeDto) {
        FieldDto fieldDto = new FieldDto(name, displayName, typeDto);
        fieldDto.setId(id);
        return fieldDto;
    }

    private CsvTestHelper() {
    }
}
