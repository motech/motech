package org.motechproject.mds.service;

import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.SettingDto;

import java.util.List;

/**
 * The <code>TypeService</code> is an interface defining available methods to execute various
 * actions on Field Types.
 */
public interface TypeService {

    void createFieldType(AvailableTypeDto type, FieldValidationDto validation, SettingDto... settings);

    List<AvailableTypeDto> getAllFieldTypes();

    void deleteFieldType(String name);
}
