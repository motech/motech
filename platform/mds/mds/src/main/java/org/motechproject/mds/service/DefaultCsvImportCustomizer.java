package org.motechproject.mds.service;


import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.exception.csv.CsvImportException;
import org.motechproject.mds.util.Constants;

import java.util.List;
import java.util.Map;

/**
 * This is a basic implementation of {@link org.motechproject.mds.service.CsvImportCustomizer}.
 *
 */
public class DefaultCsvImportCustomizer implements CsvImportCustomizer {

    @Override
    public Object findExistingInstance(Map<String, String> row, MotechDataService dataService) {
        String id = row.get(Constants.Util.ID_FIELD_DISPLAY_NAME);

        if (StringUtils.isNotBlank(id)) {
            Object object = dataService.findById(Long.valueOf(id));
            if (object == null){
                throw new CsvImportException("Unable to update, no instance with id = " + id);
            } else {
                return object;
            }
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

    @Override
    public FieldDto findField(String headerName, List<FieldDto> fieldDtos) {
        FieldDto matchingDisplayNameField = null;
        FieldDto matchingNameField = null;

        for (FieldDto field : fieldDtos) {
            if (headerName.equals(field.getBasic().getDisplayName())) {
                matchingDisplayNameField = field;
                break;
            } else if (headerName.equals(field.getBasic().getName())) {
                matchingNameField = field;
            }
        }

        return matchingDisplayNameField != null ? matchingDisplayNameField : matchingNameField;
    }
}
