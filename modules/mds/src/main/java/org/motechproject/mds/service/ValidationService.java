package org.motechproject.mds.service;

import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.dto.FieldValidationDto;

/**
 * The <code>ValidationService</code> is an interface defining available methods to execute various
 * actions on field and type validations.
 */
public interface ValidationService {

    void saveValidationForType(AvailableFieldTypeMapping type, FieldValidationDto validation);

    void deleteValidationForType(AvailableFieldTypeMapping type);
}
