package org.motechproject.mds.service;

import org.motechproject.mds.dto.AvailableTypeDto;

import java.util.List;

/**
 * This interface provides methods related with executing actions on an available types.
 */
public interface AvailableTypeService {

    List<AvailableTypeDto> getAll();

}
