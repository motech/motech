package org.motechproject.mds.service;

import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;

import java.io.IOException;
import java.util.List;

/**
 * This interface provides methods related with executing actions on an entity.
 */
public interface EntityService {

    EntityDto createEntity(EntityDto entity) throws IOException;

    List<LookupDto> saveEntityLookups(Long entityId, List<LookupDto> lookups);

    void deleteEntity(Long id);
}
