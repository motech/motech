package org.motechproject.mds.service.mapper;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.dto.EntityDto;

public final class EntityMapper {

    private EntityMapper() {
    }

    public static Entity map(EntityDto entityDto) {
        return entityDto != null ? new Entity(entityDto.getName(), entityDto.getModule()) : null;
    }
}
