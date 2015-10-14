package org.motechproject.mds.service;

import org.motechproject.mds.domain.Field;
import org.motechproject.mds.dto.FieldDto;

import java.util.Comparator;

/**
 * The <code>CsvExportCustomizer</code> interface allows to provide custom method to format related instances
 * during csv import.
 */
public interface CsvExportCustomizer {

    /**
     * Formats the field value for CSV display.
     *
     * @param object the object to format
     * @param field the field to format
     *
     * @return the formatted string that will represent the value in CSV data
     */
    String formatField(FieldDto field, Object object);

    /**
     * Allows the customizer to change the ordering of columns in the exporter file.
     * The comparator returned by this method will be used for ordering fields. Note that the
     * comparator might be requested to order fields that were not selected for export - it will be used
     * to order the entire collection of fields from the entity.
     * @return the comparator that will be used for determining the column order
     */
    Comparator<Field> columnOrderComparator();

    /**
     * Retrieves the display name for the given entity field, that will be shown in the top
     * row while exporting instances to CSV/PDF file. By default, the display name of the
     * field is used.
     *
     * @param field entity field to retrieve display name for
     * @return display name of the given field in the exported files
     */
    String exportDisplayName(Field field);
}
