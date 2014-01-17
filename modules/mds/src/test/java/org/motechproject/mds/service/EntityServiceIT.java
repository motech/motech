package org.motechproject.mds.service;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.repository.AllLookupMappings;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EntityServiceIT extends BaseIT {
    private static final String SIMPLE_NAME = "Test";

    @Autowired
    private EntityService entityService;

    @Autowired
    private AllLookupMappings allLookupMappings;

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
        String className = String.format("%s.%s", EntityBuilder.PACKAGE, "Test");
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
    public void shouldSaveEntityWithGivenLookups() throws IOException {
        List<LookupDto> lookups = Arrays.asList(new LookupDto("lookup1", true), new LookupDto("lookup2", true));
        EntityDto entityDto = new EntityDto();
        entityDto.setName("myEntity");

        entityDto = entityService.createEntity(entityDto);
        entityService.saveEntityLookups(entityDto.getId(), lookups);

        assertTrue(containsLookup("lookup1"));
        assertTrue(containsLookup("lookup2"));
    }

    @Test
    public void shouldNotAddNewLookupWhenLookupWithGivenIdAlreadyExists() throws IOException {
        List<LookupDto> lookups = Arrays.asList(new LookupDto("myLookup", true));
        EntityDto entityDto = new EntityDto();
        entityDto.setName("entity");

        entityDto = entityService.createEntity(entityDto);
        List<LookupDto> savedLookups = entityService.saveEntityLookups(entityDto.getId(), lookups);
        entityService.saveEntityLookups(entityDto.getId(), savedLookups);

        assertThat(getLookupMappings().size(), Is.is(1));
    }

    @Test
    public void shouldUpdateLookupWhenLookupWithGivenIdAlreadyExists() throws IOException {
        List<LookupDto> lookups = Arrays.asList(new LookupDto("testLookup", true));
        EntityDto entityDto = new EntityDto();
        entityDto.setName("testEntity");

        entityDto = entityService.createEntity(entityDto);
        List<LookupDto> savedLookups = entityService.saveEntityLookups(entityDto.getId(), lookups);
        savedLookups.get(0).setLookupName("newLookupName");
        savedLookups = entityService.saveEntityLookups(entityDto.getId(), savedLookups);

        assertTrue(allLookupMappings.getLookupById(savedLookups.get(0).getId()).getLookupName().equals("newLookupName"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenAddingLookupToNonExistingEntity() throws Exception {
        entityService.saveEntityLookups(9999L, Arrays.asList(new LookupDto()));
    }

    @Test(expected = EntityReadOnlyException.class)
    public void shouldThrowExceptionWhenAddingLookupToReadOnlyEntity() throws Exception {
        EntityDto entity = new EntityDto();
        entity.setReadOnly(true);
        entity.setName("readOnlyEntity");

        entity = entityService.createEntity(entity);
        entityService.saveEntityLookups(entity.getId(), Arrays.asList(new LookupDto()));
    }
}
