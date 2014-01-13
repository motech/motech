package org.motechproject.mds.service;

import org.motechproject.mds.dto.AvailableTypeDto;

import java.util.List;

/**
 * The <code>TypeService</code> is an interface defining available methods to execute various
 * actions on Field Types.
 */
public interface TypeService {

    void createFieldType(AvailableTypeDto type);

    List<AvailableTypeDto> getAllFieldTypes();
}
