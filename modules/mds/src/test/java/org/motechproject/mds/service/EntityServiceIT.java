package org.motechproject.mds.service;

import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.dto.EntityDto;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EntityServiceIT extends BaseIT {
    private static final String SIMPLE_NAME = "Test";

    @Autowired
    private EntityService entityService;

    @Test
    public void shouldCreateEntity() throws Exception {
        // given
        EntityDto entityDto = new EntityDto();
        entityDto.setName(SIMPLE_NAME);

        // when
        entityService.createEntity(entityDto);

        // then
        // 1. new entry in db should be added
        String className = String.format("%s.%s", EntityBuilder.PACKAGE, "Test");
        assertTrue(String.format("Not found %s in database", className), containsEntity(className));

        // 2. there should be ability to create a new instance of created entity
        Class<?> clazz = JDOClassLoader.PERSISTANCE_CLASS_LOADER.loadClass(className);
        Object instance = clazz.newInstance();

        assertNotNull(instance);
    }

}
