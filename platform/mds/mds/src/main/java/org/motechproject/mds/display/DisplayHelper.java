package org.motechproject.mds.display;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.mds.domain.ManyToManyRelationship;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This helper class is responsible for the parsing of relationship and combobox values
 * to display-friendly formats.
 */
public final class DisplayHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisplayHelper.class);

    private static final String ELLIPSIS = "...";

    public static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z");

    /**
     * Parses provided relationship or combobox value to display-friendly format. The type of
     * the value is determined by the {@code field} parameter. Depending on the type of the field,
     * a different representation will be returned.
     *
     * @param field field definition, that contains the type of the field
     * @param value the value to be parsed
     * @return depending on the field type, it returns {@link String} representation for one-to-one relationship
     *         {@link Map} representation for the one-to-many and many-to-many relationships
     *         {@link String} or {@link Collection} representation for comboboxes, depending on their settings
     */
    public static Object getDisplayValueForField(FieldDto field, Object value) {
        return getDisplayValueForField(field, value, null);
    }

    /**
     * Parses provided relationship or combobox value to display-friendly format. The type of
     * the value is determined by the {@code field} parameter. Depending on the type of the field,
     * a different representation will be returned. Moreover, it limits the length of the parsed values,
     * in case they exceed the provided limit.
     *
     * @param field field definition, that contains the type of the field
     * @param value the value to be parsed
     * @param maxLength the number of characters, after which the values will be trimmed; for map representation
     *                  this is a maximum number of characters for each value in the map
     * @return depending on the field type, it returns {@link String} representation for one-to-one relationship
     *         {@link Map} representation for the one-to-many and many-to-many relationships
     *         {@link String} or {@link Collection} representation for comboboxes, depending on their settings
     */
    public static Object getDisplayValueForField(FieldDto field, Object value, Integer maxLength) {
        Object displayValue = null;

        if (value == null) {
            displayValue = null;
        } else if (field.getType().isRelationship()) {
            if (field.getType().isForClass(OneToManyRelationship.class) ||
                    field.getType().isForClass(ManyToManyRelationship.class)) {
                displayValue = buildDisplayValuesMapForRelationship((Collection) value, maxLength);
            } else {
                displayValue = buildDisplayValueForRelationship(value, maxLength);
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

    private static Map<Long, String> buildDisplayValuesMapForRelationship(Collection values, Integer maxLength) {
        Map<Long, String> displayValues = new LinkedHashMap<>();
        for (Object obj : values) {
            Long key = (obj instanceof Long) ?
                    (Long) obj :
                    (Long) PropertyUtil.safeGetProperty(obj, Constants.Util.ID_FIELD_NAME);

            String value = buildDisplayValueForRelationship(obj, maxLength);

            displayValues.put(key, value);
        }
        return displayValues;
    }

    private static String buildDisplayValueForRelationship(Object value, Integer maxLength) {
        if (value instanceof Long) {
            return "#" + value;
        }

        String uiRepresentation = UIRepresentationUtil.uiRepresentationString(value);

        if (uiRepresentation != null) {
            return applyMaxLength(uiRepresentation, maxLength);
        } else if (hasCustomToString(value)) {
            String toStringResult = value.toString();
            return applyMaxLength(toStringResult, maxLength);
        } else {
            Long id = (Long) PropertyUtil.safeGetProperty(value, Constants.Util.ID_FIELD_NAME);
            return id == null ? "" : id.toString();
        }
    }

    private static String applyMaxLength(String value, Integer maxLength) {
        return maxLength != null && value.length() > maxLength ?
                value.substring(0, maxLength + 1) + ELLIPSIS : value;
    }

    private static boolean hasCustomToString(Object value) {
        Method toStringMethod = MethodUtils.getAccessibleMethod(value.getClass(), "toString", new Class[0]);
        if (toStringMethod == null ) {
            LOGGER.error("Unable to retrieve toString() method for {}", value);
            return false;
        } else {
            return !StringUtils.equals(Object.class.getName(), toStringMethod.getDeclaringClass().getName());
        }
    }

    private DisplayHelper() {
    }
}
