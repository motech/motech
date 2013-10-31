package org.motechproject.mds.service.mapper;

import org.junit.Test;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.dto.EntityDto;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class EntityMapperTest {
    @Test
    public void shouldMapEntityDtoToEntity(){
        EntityDto entityDto = new EntityDto("1", "name", "module");

        Entity entity = EntityMapper.map(entityDto);

        assertEquals(entityDto.getName(), entity.getName());
        assertEquals(entityDto.getModule(), entity.getModule());
    }

    @Test
    public void shouldReturnNullIfGivenEntityDtoIsNull(){
        assertNull(EntityMapper.map(null));
    }
}
