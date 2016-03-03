package org.motechproject.mds.service;

import org.motechproject.mds.display.DisplayHelper;
import org.motechproject.mds.dto.BrowsingSettingsDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.UIDisplayFieldComparator;
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
    public String formatField(FieldDto fieldDto, Object object) {
        Object displayValue = DisplayHelper.getDisplayValueForField(fieldDto, object);

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
    public Comparator<FieldDto> columnOrderComparator(BrowsingSettingsDto browsingSettingsDto) {
        return new UIDisplayFieldComparator(browsingSettingsDto.getDisplayedFields());
    }

    @Override
    public String exportDisplayName(FieldDto fieldDto) {
        return fieldDto.getBasic().getDisplayName();
    }

}
