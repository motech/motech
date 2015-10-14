package org.motechproject.mds.service;

import org.motechproject.mds.display.DisplayHelper;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.UIDisplayFieldComparator;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.util.TypeHelper;

import java.util.Comparator;
import java.util.Map;

/**
 * This is a basic implementation of {@link org.motechproject.mds.service.CsvExportCustomizer}.
 *
 */
public class DefaultCsvExportCustomizer implements CsvExportCustomizer {

    private static final char COLL_JOIN_CHAR = ',';

    @Override
    public String formatField(FieldDto field, Object object) {
        Object displayValue = DisplayHelper.getDisplayValueForField(field, object);

        if (displayValue == null) {
            displayValue = object;
        } else if (displayValue instanceof Map) {
            // in case of map of displays, where keys are ids or enum values, we are only concerned with
            // the values
            Map asMap = (Map) displayValue;
            displayValue = asMap.values();
        }

        return TypeHelper.format(displayValue, COLL_JOIN_CHAR);
    }

    @Override
    public Comparator<Field> columnOrderComparator() {
        return new UIDisplayFieldComparator();
    }

    @Override
    public String exportDisplayName(Field field) {
        return field.getDisplayName();
    }

}
