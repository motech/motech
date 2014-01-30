package org.motechproject.mds.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.dto.EntityDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.mds.constants.Constants.Packages;

public class EntityServiceIT extends BaseIT {
    private static final String SIMPLE_NAME = "Test";
    private static final String SIMPLE_NAME_2 = "Test2";
    private static final String SIMPLE_NAME_3 = "Test3";


    @Autowired
    private EntityService entityService;

    @Before
    public void setUp() throws Exception {
        getPersistenceManager().deletePersistentAll(getLookupMappings());
        getPersistenceManager().deletePersistentAll(getEntityMappings());
    }

    @After
    public void tearDown() throws Exception {
        getPersistenceManager().deletePersistentAll(getLookupMappings());
        getPersistenceManager().deletePersistentAll(getEntityMappings());
    }

    @Test
    public void shouldCreateEntity() throws Exception {
        // given
        EntityDto entityDto = new EntityDto();
        entityDto.setName(SIMPLE_NAME);

        // when
        entityService.createEntity(entityDto);

        // then
        // 1. new entry in db should be added
        String className = String.format("%s.%s", Packages.ENTITY, "Test");
        assertTrue(String.format("Not found %s in database", className), containsEntity(className));

        // 2. there should be ability to create a new instance of created entity
        Class<?> clazz = MDSClassLoader.PERSISTANCE.loadClass(className);
        Object instance = clazz.newInstance();

        assertNotNull(instance);

        getPersistenceManager().makePersistent(instance);

        List<?> list = cast(clazz, (Collection) getPersistenceManager().newQuery(clazz).execute());

        assertNotNull(list);
        assertFalse("The instance of entity should be saved in database", list.isEmpty());
    }

    @Test
    public void shouldRetrieveAllEntities() throws IOException {
        EntityDto entityDto = new EntityDto();

        entityDto.setName(SIMPLE_NAME_2);
        entityService.createEntity(entityDto);

        entityDto.setName(SIMPLE_NAME_3);
        entityService.createEntity(entityDto);

        List<EntityDto> result = entityService.listEntities();

        assertEquals(asList(SIMPLE_NAME_2, SIMPLE_NAME_3), extract(result, on(EntityDto.class).getName()));
    }
}
