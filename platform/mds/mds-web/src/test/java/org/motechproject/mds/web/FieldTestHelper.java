package org.motechproject.mds.web;

import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.web.domain.FieldRecord;

/**
 * Utility class for constructing minimalist fields for testing field generation.
 */
public final class FieldTestHelper {

    public static FieldDto fieldDto(Long id, String name, String className,
                                    String displayName, Object defValue) {
        FieldDto fieldDto = new FieldDto();
        fieldDto.setType(new TypeDto(className, "", "", className));
        fieldDto.setBasic(new FieldBasicDto(displayName, name));
        fieldDto.getBasic().setDefaultValue(defValue);
        fieldDto.setId(id);
        return fieldDto;
    }

    public static FieldRecord fieldRecord(String name, String className, String displayName, Object value) {
        TypeDto type = new TypeDto(className, "", "", className);
        return fieldRecord(type, name, displayName, value);
    }

    public static FieldRecord fieldRecord(TypeDto type, String name, String displayName, Object value) {
        FieldRecord record = new FieldRecord(name, value, type);
        record.setDisplayName(displayName);

        return record;
    }

    public static LookupFieldDto lookupFieldDto(Long id, String name) {
        return new LookupFieldDto(id, name, LookupFieldType.VALUE);
    }

    private FieldTestHelper() {
    }
}
