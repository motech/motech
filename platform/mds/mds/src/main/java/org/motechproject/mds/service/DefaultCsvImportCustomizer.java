package org.motechproject.mds.service;


import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.util.Constants;

import java.util.Map;

/**
 * This is a basic implementation of {@link org.motechproject.mds.service.CsvImportCustomizer}.
 *
 */
public class DefaultCsvImportCustomizer implements CsvImportCustomizer {

    @Override
    public Object findExistingInstance(Map<String, String> row, MotechDataService dataService) {
        String id = row.get(Constants.Util.ID_FIELD_NAME);

        if (StringUtils.isNotBlank(id)) {
            return dataService.findById(Long.valueOf(id));
        }
        return null;
    }

    @Override
    public Object doCreate(Object instance, MotechDataService dataService) {
        return dataService.create(instance);
    }

    @Override
    public Object doUpdate(Object instance, MotechDataService dataService) {
        return dataService.update(instance);
    }
}
