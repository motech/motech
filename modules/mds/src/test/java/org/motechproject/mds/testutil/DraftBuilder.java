package org.motechproject.mds.testutil;

import org.motechproject.mds.web.DraftData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class DraftBuilder {

    public static DraftData forNewField(String displayName, String name, String typeClass) {
        Map<String, Object> values = new HashMap<>();

        values.put(DraftData.TYPE_CLASS, typeClass);
        values.put(DraftData.DISPLAY_NAME, displayName);
        values.put(DraftData.NAME, name);

        DraftData draftData = new DraftData();

        draftData.setValues(values);
        draftData.setCreate(true);

        return draftData;
    }

    public static DraftData forFieldRemoval(Long fieldId) {
        Map<String, Object> values = new HashMap<>();
        values.put(DraftData.FIELD_ID, fieldId);

        DraftData draftData = new DraftData();
        draftData.setValues(values);
        draftData.setRemove(true);

        return draftData;
    }

    public static DraftData forFieldEdit(Long fieldId, String path, Object value) {
        Map<String, Object> values = new HashMap<>();

        values.put(DraftData.FIELD_ID, fieldId);
        values.put(DraftData.PATH, path);
        values.put(DraftData.VALUE, Arrays.asList(value));

        DraftData draftData = new DraftData();

        draftData.setValues(values);
        draftData.setEdit(true);

        return draftData;
    }

    private DraftBuilder() {
    }
}
