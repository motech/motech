package org.motechproject.mds.service;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.web.DraftData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
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
        getPersistenceManager().deletePersistentAll(getEntityMappings());
        setUpSecurityContext();
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

    @Test(expected = AccessDeniedException.class)
    public void shouldNotSaveDraftWhenUsernameIsUnavailable() throws IOException {
        EntityDto entityDto = new EntityDto();
        entityDto.setName("nousernameEntity");

        Map<String, Object> values = new HashMap<>();
        values.put("path", DraftData.ADD_NEW_INDEX);
        values.put("advanced", true);

        DraftData draftData = new DraftData();
        draftData.setType(DraftData.ADVANCED);
        draftData.setEdit(true);
        draftData.setValues(values);
        entityDto = entityService.createEntity(entityDto);

        entityService.saveDraftEntityChanges(entityDto.getId(), draftData);
        entityService.commitChanges(entityDto.getId());
    }

    @Test
    public void shouldSaveEntityWithGivenLookups() throws IOException {
        EntityDto entityDto = new EntityDto();
        entityDto.setName("myEntity");

        Map<String, Object> values = new HashMap<>();
        values.put("path", DraftData.ADD_NEW_INDEX);
        values.put("advanced", true);

        DraftData draftData = new DraftData();
        draftData.setType(DraftData.ADVANCED);
        draftData.setEdit(true);
        draftData.setValues(values);
        entityDto = entityService.createEntity(entityDto);

        entityService.saveDraftEntityChanges(entityDto.getId(), draftData);
        entityService.commitChanges(entityDto.getId());

        assertTrue(containsLookup("New lookup name"));
    }

    @Test
    public void shouldNotAddNewLookupWhenLookupWithGivenIdAlreadyExists() throws IOException {
        List<LookupDto> lookups = asList(new LookupDto("myLookup", true));
        EntityDto entityDto = new EntityDto();
        entityDto.setName("entity");

        entityDto = entityService.createEntity(entityDto);

        Map<String, Object> values = new HashMap<>();
        values.put(DraftData.PATH, DraftData.ADD_NEW_INDEX);
        values.put(DraftData.ADVANCED, true);

        DraftData dd = new DraftData();
        dd.setEdit(true);
        dd.setType(DraftData.ADVANCED);
        dd.setValues(values);

        entityService.saveDraftEntityChanges(entityDto.getId(), dd);
        //List<LookupDto> savedLookups = entityService.saveEntityLookups(entityDto.getId(), lookups);
        //entityService.saveEntityLookups(entityDto.getId(), savedLookups);

        assertThat(getLookupMappings().size(), Is.is(1));
    }

    @Test
    public void shouldUpdateLookupWhenLookupWithGivenIdAlreadyExists() throws IOException {
        List<LookupDto> lookups = asList(new LookupDto("testLookup", true));
        EntityDto entityDto = new EntityDto();
        entityDto.setName("testEntity");

        entityDto = entityService.createEntity(entityDto);
        //List<LookupDto> savedLookups = entityService.saveEntityLookups(entityDto.getId(), lookups);
        //savedLookups.get(0).setLookupName("newLookupName");
        //savedLookups = entityService.saveEntityLookups(entityDto.getId(), savedLookups);

        //assertTrue(allLookupMappings.getLookupById(savedLookups.get(0).getId()).getLookupName().equals("newLookupName"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenAddingLookupToNonExistingEntity() throws Exception {
        //entityService.
        //entityService.saveEntityLookups(9999L, asList(new LookupDto()));
    }

    @Test(expected = EntityReadOnlyException.class)
    public void shouldThrowExceptionWhenAddingLookupToReadOnlyEntity() throws Exception {
        EntityDto entity = new EntityDto();
        entity.setReadOnly(true);
        entity.setName("readOnlyEntity");

        entity = entityService.createEntity(entity);
        //entityService.saveEntityLookups(entity.getId(), asList(new LookupDto()));
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

    private void setUpSecurityContext() {
        SecurityContext securityContext = new SecurityContextImpl();
        Authentication authentication = new UsernamePasswordAuthenticationToken(new User("motech", "motech", asList(new SimpleGrantedAuthority("seussSchemaAccess"))), null);
        securityContext.setAuthentication(authentication);
        authentication.setAuthenticated(false);
        SecurityContextHolder.setContext(securityContext);
    }
}
