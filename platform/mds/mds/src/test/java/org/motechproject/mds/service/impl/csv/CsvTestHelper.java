package org.motechproject.mds.service.impl.csv;

import org.joda.time.DateTime;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldMetadata;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeSetting;
import org.motechproject.mds.testutil.records.Record2;
import org.motechproject.mds.testutil.records.RecordEnum;
import org.motechproject.mds.testutil.records.RelatedClass;
import org.motechproject.mds.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

public class CsvTestHelper {
    
    public static void mockRecord2Fields(Entity mockEntity) {
        List<Field> fields = new ArrayList<>();

        fields.add(new Field(mockEntity, "id", "ID", new Type(Long.class)));
        fields.add(new Field(mockEntity, "creator", "Creator", new Type(String.class)));
        fields.add(new Field(mockEntity, "owner", "Owner", new Type(String.class)));
        fields.add(new Field(mockEntity, "modifiedBy", "Modified By", new Type(String.class)));
        fields.add(new Field(mockEntity, "creationDate", "Creation date", new Type(DateTime.class)));
        fields.add(new Field(mockEntity, "modificationDate", "Modification date", new Type(DateTime.class)));
        fields.add(new Field(mockEntity, "value", "Value Disp", new Type(String.class)));
        fields.add(new Field(mockEntity, "date", "Date disp", new Type(Date.class)));
        fields.add(new Field(mockEntity, "dateIgnoredByRest", "dateIgnoredByRest disp", new Type(Date.class)));
        fields.add(comboboxField(mockEntity, "enumField", false));
        fields.add(comboboxField(mockEntity, "enumListField", true));
        fields.add(relationshipField(mockEntity, "singleRelationship", OneToOneRelationship.class));
        fields.add(relationshipField(mockEntity, "multiRelationship", OneToManyRelationship.class, ArrayList.class));

        when(mockEntity.getFields()).thenReturn(fields);

        when(mockEntity.getClassName()).thenReturn(Record2.class.getName());
    }


    private static Field comboboxField(Entity mockEntity, String name, boolean isList) {
        Field field = new Field(mockEntity, name, name + " Disp", new Type("mds.field.combobox", "desc", List.class));

        field.addMetadata(new FieldMetadata(field, Constants.MetadataKeys.ENUM_CLASS_NAME, RecordEnum.class.getName()));

        TypeSetting typeSetting = new TypeSetting(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS);
        typeSetting.setValueType(new Type(Boolean.class));

        FieldSetting fieldSetting = new FieldSetting(field, typeSetting);
        fieldSetting.setValue(String.valueOf(isList));
        field.addSetting(fieldSetting);

        return field;
    }

    private static Field relationshipField(Entity mockEntity, String name, Class relationshipType) {
        return relationshipField(mockEntity, name, relationshipType, null);
    }

    private static Field relationshipField(Entity mockEntity, String name, Class relationshipType, Class collectionType) {
        Field field = new Field(mockEntity, name, name + " Disp", new Type(relationshipType));
        field.addMetadata(new FieldMetadata(field, Constants.MetadataKeys.RELATED_CLASS, RelatedClass.class.getName()));
        if (collectionType != null) {
            field.addMetadata(new FieldMetadata(field, Constants.MetadataKeys.RELATIONSHIP_COLLECTION_TYPE, collectionType.getName()));
        }
        return field;
    }

    private CsvTestHelper() {
    }
}
