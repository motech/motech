package org.motechproject.mds.helper;

import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.TypeDto;

import java.util.HashMap;
import java.util.Map;

/**
 * The <code>ActionParameterTypeResolver</code> utility class provides a method that resolves tasks parameter type
 * name based on entity field type.
 *
 * @see org.motechproject.mds.domain.Field
 * @see org.motechproject.mds.domain.Type
 */
public final class ActionParameterTypeResolver {
    private static final String UNICODE = "UNICODE";
    private static final String TEXTAREA = "TEXTAREA";
    private static final String INTEGER = "INTEGER";
    private static final String BOOLEAN = "BOOLEAN";
    private static final String DATE = "DATE";
    private static final String TIME = "TIME";
    private static final String DOUBLE = "DOUBLE";
    private static final String MAP = "MAP";
    private static final String LONG = "LONG";
    private static final String UNKNOWN = "UNKNOWN";
    private static final String LIST = "LIST";
    private static final String SELECT = "SELECT";

    private ActionParameterTypeResolver() {
    }

    private static final Map<String, String> TYPE_MAPPING = new HashMap<>();

    static {
        TYPE_MAPPING.put("mds.field.integer", INTEGER);
        TYPE_MAPPING.put("mds.field.string", UNICODE);
        TYPE_MAPPING.put("mds.field.textArea", TEXTAREA);
        TYPE_MAPPING.put("mds.field.boolean", BOOLEAN);
        TYPE_MAPPING.put("mds.field.date", DATE);
        TYPE_MAPPING.put("mds.field.date8", DATE);
        TYPE_MAPPING.put("mds.field.time", TIME);
        TYPE_MAPPING.put("mds.field.datetime", DATE);
        TYPE_MAPPING.put("mds.field.datetime8", DATE);
        TYPE_MAPPING.put("mds.field.decimal", DOUBLE);
        TYPE_MAPPING.put("mds.field.map", MAP);
        TYPE_MAPPING.put("mds.field.period", UNICODE); // no more suitable type at the moment
        TYPE_MAPPING.put("mds.field.locale", UNICODE); // no more suitable type at the moment
        TYPE_MAPPING.put("mds.field.localDate", DATE);
        TYPE_MAPPING.put("mds.field.long", LONG);
        TYPE_MAPPING.put("mds.field.relationship", UNKNOWN); // it is not supposed to occur
        TYPE_MAPPING.put("mds.field.relationship.oneToMany", LIST);
        TYPE_MAPPING.put("mds.field.relationship.oneToOne", LONG);
        TYPE_MAPPING.put("mds.field.relationship.manyToOne", LONG);
        TYPE_MAPPING.put("mds.field.relationship.manyToMany", LIST);
    }
    // mds.field.combobox resolved separately

    /**
     * Resolves correct task parameter type, based on the MDS field type.
     *
     * @param field MDS field
     * @return matching task parameter type
     */
    public static String resolveType(EntityDto entity, FieldDto field) {
        TypeDto type = field.getType();
        if (type.isCombobox()) {
            return resolveComboboxType(new ComboboxHolder(entity, field));
        } else {
            String matchedType = TYPE_MAPPING.get(type.getDisplayName());
            return null != matchedType ? matchedType : UNKNOWN;
        }
    }

    private static String resolveComboboxType(ComboboxHolder comboboxHolder) {
        if (comboboxHolder.isAllowMultipleSelections()) {
            return LIST;
        } else {
            return SELECT;
        }
    }
}
