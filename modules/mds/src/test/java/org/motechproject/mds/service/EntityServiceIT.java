package org.motechproject.mds.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.testutil.DraftBuilder;
import org.motechproject.mds.web.DraftData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.mds.util.Constants.Packages;

public class EntityServiceIT extends BaseIT {
    private static final String SIMPLE_NAME = "Test";
    private static final String SIMPLE_NAME_2 = "Test2";
    private static final String SIMPLE_NAME_3 = "Test3";

    @Autowired
    private EntityService entityService;

    @Autowired
    private TypeService typeService;

    @Before
    public void setUp() throws Exception {
        clearDB();
        setUpSecurityContext();
    }

    @After
    public void tearDown() throws Exception {
        clearDB();
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
        Class<?> clazz = MDSClassLoader.getInstance().loadClass(className);
        Object instance = clazz.newInstance();

        assertNotNull(instance);

        getPersistenceManager().makePersistent(instance);

        List<?> list = cast(clazz, (Collection) getPersistenceManager().newQuery(clazz).execute());

        assertNotNull(list);
        assertFalse("The instance of entity should be saved in database", list.isEmpty());
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotSaveDraftWhenEntityIsUnavailable() throws IOException {
        Map<String, Object> values = new HashMap<>();
        values.put("path", DraftData.ADD_NEW_INDEX);
        values.put("advanced", true);

        DraftData draftData = new DraftData();
        draftData.setEdit(true);
        draftData.setValues(values);

        entityService.saveDraftEntityChanges(123L, draftData);
    }

    @Test
    public void shouldSaveEntityWithGivenLookups() throws IOException {
        EntityDto entityDto = new EntityDto();
        entityDto.setName("myEntity");

        Map<String, Object> values = new HashMap<>();
        values.put("path", DraftData.ADD_NEW_INDEX);
        values.put("advanced", true);

        DraftData draftData = new DraftData();
        draftData.setEdit(true);
        draftData.setValues(values);
        entityDto = entityService.createEntity(entityDto);

        entityService.saveDraftEntityChanges(entityDto.getId(), draftData);
        entityService.commitChanges(entityDto.getId());

        assertTrue(containsLookup("New lookup"));
    }

    @Test
    public void shouldRetrieveAllEntities() throws IOException {
        entityService.createEntity(new EntityDto(null, null, SIMPLE_NAME_2, null, null));
        entityService.createEntity(new EntityDto(null, null, SIMPLE_NAME_3, null, null));

        List<EntityDto> result = entityService.listEntities();

        List<String> expected = asList(SIMPLE_NAME_2, SIMPLE_NAME_3);
        List<String> actual = extract(result, on(EntityDto.class).getName());

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldRetrieveAllWorkInProgress() throws IOException {
        EntityDto entityDto = new EntityDto();

        entityDto.setName("WIP1");
        EntityDto result1 = entityService.createEntity(entityDto);

        entityDto.setClassName(null);
        entityDto.setName("WIP2");
        EntityDto result2 = entityService.createEntity(entityDto);

        entityService.saveDraftEntityChanges(result1.getId(),
                DraftBuilder.forNewField("disp", "name", Integer.class.getName()));
        entityService.saveDraftEntityChanges(result2.getId(),
                DraftBuilder.forNewField("disp", "name", Integer.class.getName()));

        List<EntityDto> wip = entityService.listWorkInProgress();

        assertNotNull(wip);

        List<String> result = extract(wip, on(EntityDto.class).getName());
        Collections.sort(result);
        assertEquals(asList("WIP1", "WIP2"), result);
    }

    @Test
    public void testDraftWorkflow() throws IOException {
        EntityDto entityDto = new EntityDto();
        entityDto.setName("DraftTest");
        entityDto = entityService.createEntity(entityDto);

        final Long entityId = entityDto.getId();

        entityService.saveDraftEntityChanges(entityId,
                DraftBuilder.forNewField("disp", "f1name", Integer.class.getName()));

        List<FieldDto> fields = entityService.getFields(entityId);
        assertNotNull(fields);
        assertEquals(asList("f1name"),
                extract(fields, on(FieldDto.class).getBasic().getName()));

        // check if abandoning works
        entityService.abandonChanges(entityId);
        fields = entityService.getFields(entityId);

        assertNotNull(fields);
        assertTrue(fields.isEmpty());

        // check add-edit-commit
        entityService.saveDraftEntityChanges(entityId,
                DraftBuilder.forNewField("disp", "f1name", Integer.class.getName()));

        fields = entityService.getFields(entityId);

        assertNotNull(fields);
        assertEquals(1, fields.size());
        FieldDto field = fields.get(0);

        entityService.saveDraftEntityChanges(entityDto.getId(),
                DraftBuilder.forFieldEdit(field.getId(), "basic.displayName", "newDisp"));

        fields = entityService.getFields(entityId);

        assertNotNull(fields);
        assertEquals(asList("newDisp"),
                extract(fields, on(FieldDto.class).getBasic().getDisplayName()));

        entityService.commitChanges(entityId);

        // check if changes were persisted in db
        Entity entityFromDb = getEntities().get(0);
        assertEquals(1, entityFromDb.getFields().size());

        Field fieldFromDb = entityFromDb.getField("f1name");
        assertNotNull(fieldFromDb);
        assertEquals("newDisp", fieldFromDb.getDisplayName());

        // no drafts in db
        assertTrue(entityService.listWorkInProgress().isEmpty());
    }

    private void setUpSecurityContext() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("mdsSchemaAccess");
        List<SimpleGrantedAuthority> authorities = asList(authority);

        User principal = new User("motech", "motech", authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null);
        authentication.setAuthenticated(false);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
