package org.motechproject.mds.service;

import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.UIDisplayFieldComparator;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.PropertyUtil;

import java.util.Collection;
import java.util.Comparator;

/**
 * This is a basic implementation of {@link org.motechproject.mds.service.CsvExportCustomizer}.
 *
 */
public class DefaultCsvExportCustomizer implements CsvExportCustomizer {

    @Override
    public String formatRelationship(Object object) {
        if (object instanceof Collection) {
            int i = 0;
            StringBuilder sb = new StringBuilder();
            for (Object item : (Collection) object) {
                if (i++ != 0) {
                    sb.append(',');
                }
                sb.append(PropertyUtil.safeGetProperty(item, Constants.Util.ID_FIELD_NAME));
            }
            return sb.toString();
        } else if (object != null) {
            return String.valueOf(PropertyUtil.safeGetProperty(object, Constants.Util.ID_FIELD_NAME));
        } else {
            return "";
        }
    }

    @Override
    public Comparator<Field> columnOrderComparator() {
        return new UIDisplayFieldComparator();
    }
}
