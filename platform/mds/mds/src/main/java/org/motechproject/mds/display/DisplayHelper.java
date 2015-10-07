package org.motechproject.mds.display;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.mds.domain.ManyToManyRelationship;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DisplayHelper {

    private static final String ELLIPSIS = "...";
    private static final Integer TO_STRING_MAX_LENGTH = 80;
    private static final Integer UI_REPRESENTATION_MAX_LENGTH = 80;

    public static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z");

    public static Object getDisplayValueForField(FieldDto field, Object value) {
        Object displayValue = null;
        if (field.getType().isRelationship()) {
            if (field.getType().isForClass(OneToManyRelationship.class) ||
                    field.getType().isForClass(ManyToManyRelationship.class)) {
                displayValue = buildDisplayValuesMap((Collection) value);
            } else {
                if (value != null) {
                    displayValue = buildDisplayValue(value);
                }
            }
        } else if (field.getType().isCombobox()) {
            displayValue = getDisplayValueForCombobox(field, value);
        }

        return displayValue;
    }

    private static Object getDisplayValueForCombobox(FieldDto field, Object value) {
        Object displayValue;
        if (Constants.Util.FALSE.equalsIgnoreCase(field.getSettingsValueAsString(Constants.Settings.ALLOW_USER_SUPPLIED))) {
            String mapString = field.getSettingsValueAsString(Constants.Settings.COMBOBOX_VALUES);

            Map<String, String> comboboxValues = TypeHelper.parseStringToMap(String.class, String.class, mapString);

            if (value instanceof Collection) {
                Collection valuesToDisplay = new ArrayList();
                Collection enumList = (Collection) value;
                for (Object enumValue : enumList) {
                    String valueFromMap = comboboxValues.get(ObjectUtils.toString(enumValue));
                    valuesToDisplay.add(StringUtils.isNotEmpty(valueFromMap) ? valueFromMap : enumValue);
                }
                displayValue = valuesToDisplay;
            } else {
                String valueFromMap = comboboxValues.get(ObjectUtils.toString(value));
                displayValue = StringUtils.isNotEmpty(valueFromMap) ? valueFromMap : value;
            }
        } else {
            displayValue = value;
        }

        return displayValue;
    }

    private static Map<Long, String> buildDisplayValuesMap(Collection values) {
        Map<Long, String> displayValues = new LinkedHashMap<>();
        for (Object obj : values) {
            Long key = (Long) PropertyUtil.safeGetProperty(obj, Constants.Util.ID_FIELD_NAME);
            String uiRepresentation = UIRepresentationUtil.uiRepresentationString(obj);

            if (uiRepresentation != null) {
                displayValues.put(key, uiRepresentation.length() > UI_REPRESENTATION_MAX_LENGTH ?
                        uiRepresentation.substring(0, UI_REPRESENTATION_MAX_LENGTH + 1) + ELLIPSIS : uiRepresentation);
            } else {
                String toStringResult = obj.toString();
                displayValues.put(key, toStringResult.length() > TO_STRING_MAX_LENGTH ?
                        toStringResult.substring(0, TO_STRING_MAX_LENGTH + 1) + ELLIPSIS : toStringResult);
            }
        }
        return displayValues;
    }

    private static String buildDisplayValue(Object value) {
        String uiRepresentation = UIRepresentationUtil.uiRepresentationString(value);
        if (uiRepresentation != null) {
            return uiRepresentation.length() > UI_REPRESENTATION_MAX_LENGTH ?
                    uiRepresentation.substring(0, UI_REPRESENTATION_MAX_LENGTH + 1) + ELLIPSIS : uiRepresentation;
        } else {
            String toStringResult = value.toString();
            return toStringResult.length() > TO_STRING_MAX_LENGTH ?
                    toStringResult.substring(0, TO_STRING_MAX_LENGTH + 1) + ELLIPSIS : toStringResult;
        }
    }

    private DisplayHelper() {
    }
}
