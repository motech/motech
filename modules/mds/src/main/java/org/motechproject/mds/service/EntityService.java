package org.motechproject.mds.service;

import org.motechproject.mds.dto.EntityDto;

import java.io.IOException;

/**
 * This interface provides methods related with executing actions on an entity.
 */
public interface EntityService {

    EntityDto createEntity(EntityDto entity) throws IOException;

}
