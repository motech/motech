package org.motechproject.mds.it.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldMetadata;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeSetting;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.DraftData;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.UserPreferencesDto;
import org.motechproject.mds.exception.entity.EntityAlreadyExistException;
import org.motechproject.mds.exception.entity.EntityNotFoundException;
import org.motechproject.mds.it.BaseIT;
import org.motechproject.mds.osgi.EntitiesBundleMonitor;
import org.motechproject.mds.repository.internal.AllEntityDrafts;
import org.motechproject.mds.repository.internal.AllTypes;
import org.motechproject.mds.repository.internal.MetadataHolder;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.TypeService;
import org.motechproject.mds.service.UserPreferencesService;
import org.motechproject.mds.testutil.DraftBuilder;
import org.motechproject.mds.testutil.FieldTestHelper;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.SecurityMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.mds.dto.LookupFieldType.RANGE;
import static org.motechproject.mds.dto.LookupFieldType.SET;
import static org.motechproject.mds.dto.LookupFieldType.VALUE;
import static org.motechproject.mds.testutil.LookupTestHelper.lookupFieldsFromNames;
import static org.motechproject.mds.util.Constants.MetadataKeys.OWNING_SIDE;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_FIELD;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATIONSHIP_COLLECTION_TYPE;

public class EntityServiceContextIT extends BaseIT {
    private static final String SIMPLE_NAME = "Test";
    private static final String SIMPLE_NAME_2 = "Test2";
    private static final String SIMPLE_NAME_3 = "Test3";

    @Autowired
    private EntityService entityService;

    @Autowired
    private JarGeneratorService jarGeneratorService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private MetadataHolder metadataHolder;

    @Autowired
    private EntitiesBundleMonitor monitor;

    @Autowired
    private AllTypes allTypes;

    @Autowired
    private AllEntityDrafts allEntityDrafts;

    @Autowired
    private UserPreferencesService userPreferencesService;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        setUpSecurityContext();
        metadataHolder.reloadMetadata();

        Path path = Paths.get(monitor.bundleLocation());
        Files.deleteIfExists(path);
    }

    @Test
    public void shouldCreateEntity() throws Exception {
        // given
        EntityDto entityDto = new EntityDto();
        entityDto.setName(SIMPLE_NAME);

        // when
        entityService.createEntity(entityDto);
        setProperty(monitor, "bundleStarted", true);
        setProperty(monitor, "bundleInstalled", true);
        setProperty(monitor, "contextInitialized", true);

        SchemaHolder schemaHolder = entityService.getSchema();
        jarGeneratorService.regenerateMdsDataBundle(schemaHolder);

        // then
        // 1. new entry in db should be added
        String className = String.format("%s.%s", Constants.PackagesGenerated.ENTITY, "Test");
        assertTrue(String.format("Not found %s in database", className), containsEntity(className));

        // 2. there should be ability to create a new instance of created entity
        Class<?> clazz = MDSClassLoader.getInstance().loadClass(className);
        Object instance = clazz.newInstance();

        assertNotNull(instance);

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            // We want to use the current factory for persisting so this hack is required
            // Normally these classes live in a separate bundle
            Thread.currentThread().setContextClassLoader(MDSClassLoader.getInstance());
            getPersistenceManagerFactory().registerMetadata(metadataHolder.getJdoMetadata());

            getPersistenceManager().makePersistent(instance);

            List<?> list = cast(clazz, (Collection) getPersistenceManager().newQuery(clazz).execute());

            assertNotNull(list);
            assertFalse("The instance of entity should be saved in database", list.isEmpty());
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
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

    @Test(expected = EntityAlreadyExistException.class)
    public void shouldNotSaveEntityOfTheSameName() throws IOException {
        String testEntityName1 = "TestEntity";
        String testEntityName2 = "TeStEnTiTy";

        EntityDto entityDto1 = new EntityDto();
        entityDto1.setName(testEntityName1);

        EntityDto entityDto2 = new EntityDto();
        entityDto2.setName(testEntityName2);

        entityService.createEntity(entityDto1);
        entityService.createEntity(entityDto2);
    }

    @Test
    public void shouldSaveEntityWithGivenLookups() throws IOException {
        EntityDto entityDto = new EntityDto();
        entityDto.setName("myEntity");

        Map<String, Object> values = new HashMap<>();
        values.put("path", DraftData.ADD_NEW_INDEX);
        values.put("advanced", true);
        values.put("value", asList("Lookup 1"));

        DraftData draftData = new DraftData();
        draftData.setEdit(true);
        draftData.setValues(values);
        entityDto = entityService.createEntity(entityDto);

        entityService.saveDraftEntityChanges(entityDto.getId(), draftData);
        entityService.commitChanges(entityDto.getId());

        assertTrue(containsLookup("Lookup 1", entityDto.getId()));
    }

    @Test
    public void shouldAddNewFieldForLookupAndSaveEntity() throws IOException {
        EntityDto entityDto = new EntityDto();
        entityDto.setName("myEntity");
        entityDto = entityService.createEntity(entityDto);

        //add a new field to draft
        EntityDraft entityDraft = entityService.getEntityDraft(entityDto.getId());
        entityService.saveDraftEntityChanges(entityDraft.getId(), DraftBuilder.forNewField("disp", "testFieldName", Long.class.getName()));
        FieldDto field = selectFirst(entityService.getFields(entityDraft.getId()), having(on(FieldDto.class).getBasic().getName(), equalTo("testFieldName")));

        LookupDto lookup = new LookupDto("lookup", false, false, null, true);
        entityService.addLookups(entityDraft.getId(), Collections.singletonList(lookup));

        Map<String, Object> values = new HashMap<>();
        values.put("path", "indexes.0.$addField");
        values.put("advanced", true);
        values.put("value", Collections.singletonList(field.getId()));
        DraftData draftData = new DraftData();
        draftData.setEdit(true);
        draftData.setValues(values);

        entityService.saveDraftEntityChanges(entityDraft.getId(), draftData);
        entityService.commitChanges(entityDto.getId());

        assertNotNull(entityService.getLookupByName(entityDto.getId(),"lookup").getLookupField("testFieldName"));
    }

    @Test
    public void shouldRetrieveAllEntities() throws IOException {
        entityService.createEntity(new EntityDto(null, null, SIMPLE_NAME_2, null, null, SecurityMode.EVERYONE, null));
        entityService.createEntity(new EntityDto(null, null, SIMPLE_NAME_3, null, null, SecurityMode.EVERYONE, null));

        List<EntityDto> result = entityService.listEntities();

        List<String> expected = asList(SIMPLE_NAME_2, SIMPLE_NAME_3);
        List<String> actual = extract(result, on(EntityDto.class).getName());

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldRetrieveOnlyEntitiesUserHasAccessTo() throws IOException {
        entityService.createEntity(new EntityDto(null, null, SIMPLE_NAME, null, null, SecurityMode.EVERYONE, null));
        entityService.createEntity(new EntityDto(null, null, SIMPLE_NAME_2, null, null,
                SecurityMode.USERS, new HashSet<>(Arrays.asList("motech", "motech2"))));
        entityService.createEntity(new EntityDto(null, null, SIMPLE_NAME_3, null, null,
                SecurityMode.USERS, new HashSet<>(Arrays.asList("no_motech", "definitely_no_motech"))));

        List<EntityDto> result = entityService.listEntities(true);

        List<String> expected = asList(SIMPLE_NAME, SIMPLE_NAME_2);
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
                DraftBuilder.forNewField("disp", "name", Long.class.getName()));
        entityService.saveDraftEntityChanges(result2.getId(),
                DraftBuilder.forNewField("disp", "name", Long.class.getName()));

        List<EntityDto> wip = entityService.listWorkInProgress();

        assertNotNull(wip);

        List<String> result = extract(wip, on(EntityDto.class).getName());
        Collections.sort(result);
        assertEquals(asList("WIP1", "WIP2"), result);
    }

    @Test
    public void shouldFindEntitiesByPackage() {
        entityService.createEntity(new EntityDto(
                null, "org.motechproject.ivr.Test", SIMPLE_NAME, null, null, SecurityMode.EVERYONE, null));

        entityService.createEntity(new EntityDto(
                null, "org.motechproject.csd.domain.Address", "Address", null, null, SecurityMode.EVERYONE, null));

        entityService.createEntity(new EntityDto(
                null, "org.motechproject.csd.another.Provider", "Provider", null, null, SecurityMode.EVERYONE, null));

        List<EntityDto> entities = entityService.findEntitiesByPackage("org.motechproject.csd");

        assertNotNull(entities);

        List<String> result = extract(entities, on(EntityDto.class).getName());
        Collections.sort(result);
        assertEquals(asList("Address", "Provider"), result);
    }

    @Test
    public void shouldFindEntitiesByBundle() {
        EntityDto lessons = new EntityDto(null, "org.motechproject.mtraining.domain.Lesson", "Lesson", "mtraining", null,
                SecurityMode.EVERYONE, null);
        lessons.setBundleSymbolicName("org.motechproject.mtraining");
        entityService.createEntity(lessons);

        EntityDto chapters = new EntityDto(null, "org.motechproject.mtraining.domain.Chapter", "Chapter", "mtraining", null,
                SecurityMode.EVERYONE, null);
        chapters.setBundleSymbolicName("org.motechproject.mtraining");
        entityService.createEntity(chapters);

        EntityDto wrong = new EntityDto(null, "org.motechproject.mtraining.domain.Wrong", "Chapter", "mtraining", null,
                SecurityMode.EVERYONE, null);
        wrong.setBundleSymbolicName("org.motechproject.other");
        entityService.createEntity(wrong);

        List<EntityDto> entities = entityService.listEntitiesByBundle("org.motechproject.mtraining");

        assertNotNull(entities);

        List<String> result = extract(entities, on(EntityDto.class).getName());
        Collections.sort(result);
        assertEquals(asList("Chapter", "Lesson"), result);
    }

    @Test
    public void testDraftWorkflow() throws IOException {
        EntityDto entityDto = new EntityDto();
        entityDto.setName("DraftTest");
        entityDto = entityService.createEntity(entityDto);

        final Long entityId = entityDto.getId();

        entityService.saveDraftEntityChanges(entityId,
                DraftBuilder.forNewField("disp", "f1name", Long.class.getName()));

        List<FieldDto> fields = entityService.getFields(entityId);
        assertNotNull(fields);
        assertEquals(asList("id", "creator", "owner", "modifiedBy", "creationDate", "modificationDate", "f1name"),
                extract(fields, on(FieldDto.class).getBasic().getName()));

        // check if abandoning works
        entityService.abandonChanges(entityId);
        fields = entityService.getFields(entityId);

        assertNotNull(fields);
        assertEquals(asList("id", "creator", "owner", "modifiedBy", "creationDate", "modificationDate"),
                extract(fields, on(FieldDto.class).getBasic().getName()));

        // check add-edit-commit
        entityService.saveDraftEntityChanges(entityId,
                DraftBuilder.forNewField("disp", "f1name", Long.class.getName()));

        fields = entityService.getFields(entityId);

        assertNotNull(fields);
        assertEquals(7, fields.size());
        FieldDto field = selectFirst(fields, having(on(FieldDto.class).getBasic().getName(), equalTo("f1name")));

        entityService.saveDraftEntityChanges(entityDto.getId(),
                DraftBuilder.forFieldEdit(field.getId(), "basic.displayName", "newDisp"));

        fields = entityService.getFields(entityId);

        assertNotNull(fields);
        assertEquals(asList("Id", "Created By", "Owner", "Modified By", "Creation Date", "Modification Date", "newDisp"),
                extract(fields, on(FieldDto.class).getBasic().getDisplayName()));
        entityService.commitChanges(entityId);

        // check if changes were persisted in db
        Entity entityFromDb = getEntities().get(0);
        assertEquals(7, entityFromDb.getFields().size());

        Field fieldFromDb = entityFromDb.getField("f1name");
        assertNotNull(fieldFromDb);
        assertEquals("newDisp", fieldFromDb.getDisplayName());

        // no drafts in db
        assertTrue(entityService.listWorkInProgress().isEmpty());
    }

    @Test
    public void shouldNotAddFieldsWithTheSameNameToAnEntity() throws IOException {
        EntityDto entityDto = new EntityDto();
        entityDto.setName("SameFieldTest");
        entityDto = entityService.createEntity(entityDto);

        FieldDto field1 = new FieldDto();
        field1.setBasic(new FieldBasicDto("disp", "name"));
        field1.setType(typeService.findType(Boolean.class));
        FieldDto field2 = new FieldDto();
        field2.setBasic(new FieldBasicDto("dispName2", "name"));
        field2.setType(typeService.findType(Integer.class));

        entityService.addFields(entityDto, asList(field1, field2));

        List<FieldDto> fieldsFromDb = entityService.getFields(entityDto.getId());

        assertNotNull(fieldsFromDb);
        assertEquals(7, fieldsFromDb.size());
        assertEquals(asList("Id", "Created By", "Owner", "Modified By", "Creation Date", "Modification Date", "dispName2"),
                extract(fieldsFromDb, on(FieldDto.class).getBasic().getDisplayName()));
    }

    @Test
    public void shouldAddLookups() throws IOException {
        EntityDto entityDto = new EntityDto();
        entityDto.setName("LookupTestEnt");
        entityDto = entityService.createEntity(entityDto);

        FieldDto boolField = FieldTestHelper.fieldDto("boolField", Boolean.class);
        FieldDto dtField = FieldTestHelper.fieldDto("dtField", DateTime.class);
        FieldDto strField = FieldTestHelper.fieldDto("strField", String.class);

        entityService.addFields(entityDto, asList(boolField, dtField, strField));

        List<LookupFieldDto> lookupFieldDtos = lookupFieldsFromNames("boolField", "dtField", "strField");
        lookupFieldDtos.get(1).setType(RANGE);
        lookupFieldDtos.get(2).setType(SET);

        LookupDto lookup = new LookupDto("lookup", false, false, lookupFieldDtos, true);

        entityService.addLookups(entityDto.getId(), asList(lookup));

        LookupDto lookupFromDb = entityService.getLookupByName(entityDto.getId(), "lookup");

        assertNotNull(lookupFromDb);
        assertEquals("lookup", lookupFromDb.getLookupName());
        assertEquals("lookup", lookupFromDb.getMethodName());
        assertEquals(asList("boolField", "dtField", "strField"),
                extract(lookupFromDb.getLookupFields(), on(LookupFieldDto.class).getName()));
        assertEquals(asList(VALUE, RANGE, SET),
                extract(lookupFromDb.getLookupFields(), on(LookupFieldDto.class).getType()));
        assertEquals(asList("boolField", "dtField", "strField"), lookupFromDb.getFieldsOrder());
    }

    @Test
    public void shouldUpdateRestOptions() throws IOException {
        // given
        EntityDto entityDto = new EntityDto();
        entityDto.setName("RestTestEnt");
        entityDto = entityService.createEntity(entityDto);

        FieldDto boolField = FieldTestHelper.fieldDto("boolField", Boolean.class);
        FieldDto dtField = FieldTestHelper.fieldDto("dtField", DateTime.class);
        FieldDto strField = FieldTestHelper.fieldDto("strField", String.class);

        entityService.addFields(entityDto, asList(boolField, dtField, strField));

        List<LookupFieldDto> lookupFieldDtos = lookupFieldsFromNames("boolField", "dtField", "strField");
        LookupDto lookup = new LookupDto("lookup", false, false, lookupFieldDtos, true);

        List<LookupFieldDto> strLookupFieldDtos = lookupFieldsFromNames("strField");
        LookupDto strLookup = new LookupDto("strLookup", false, false, strLookupFieldDtos, true);

        entityService.addLookups(entityDto.getId(), asList(lookup, strLookup));

        RestOptionsDto restOptionsDto = new RestOptionsDto(true, false, true, false, false);

        restOptionsDto.addField("boolField");
        restOptionsDto.addField("strField");
        restOptionsDto.addLookup("strLookup");

        // when
        entityService.updateRestOptions(entityDto.getId(), restOptionsDto);

        // then
        AdvancedSettingsDto advancedSettingsDto = entityService.getAdvancedSettings(entityDto.getId(), true);
        RestOptionsDto fromDb = advancedSettingsDto.getRestOptions();

        assertTrue(fromDb.isCreate());
        assertFalse(fromDb.isRead());
        assertTrue(fromDb.isUpdate());
        assertFalse(fromDb.isDelete());

        assertEquals(asList("strLookup"), fromDb.getLookupNames());
        assertEquals(asList("boolField", "strField"), fromDb.getFieldNames());
    }

    @Test
    public void testRelatedFieldGenerationForManyToManyRelationship() {
        EntityDto entityDto1 = new EntityDto();
        entityDto1.setName("RelationTestEnt1");
        entityDto1 = entityService.createEntity(entityDto1);
        EntityDto entityDto2 = new EntityDto();
        entityDto2.setName("RelationTestEnt2");
        entityDto2 = entityService.createEntity(entityDto2);
        EntityDto entityDto3 = new EntityDto();
        entityDto3.setName("RelationTestEnt3");
        entityDto3 = entityService.createEntity(entityDto3);

        EntityDraft entityDraft1 = entityService.getEntityDraft(entityDto1.getId());
        Set<Lookup> fieldLookups = new HashSet<>();
        Field field = new Field(entityDraft1, "newField", "Display Name", fieldLookups);
        Type type = allTypes.retrieveByClassName(TypeDto.MANY_TO_MANY_RELATIONSHIP.getTypeClass());
        field.setType(type);
        if (type.hasSettings()) {
            for (TypeSetting setting : type.getSettings()) {
                field.addSetting(new FieldSetting(field, setting));
            }
        }

        FieldMetadata metadata = new FieldMetadata(field, RELATED_CLASS);
        metadata.setValue(entityDto2.getClassName());
        field.addMetadata(metadata);
        metadata = new FieldMetadata(field, RELATIONSHIP_COLLECTION_TYPE);
        metadata.setValue("java.util.Set");
        field.addMetadata(metadata);
        metadata = new FieldMetadata(field, RELATED_FIELD);
        metadata.setValue("relatedField");
        field.addMetadata(metadata);
        metadata = new FieldMetadata(field, OWNING_SIDE);
        metadata.setValue("true");
        entityDraft1.addField(field);
        allEntityDrafts.update(entityDraft1);

        entityService.commitChanges(entityDto1.getId());

        FieldDto relatedField = getField(entityDto2.getId(), "relatedField");

        //Changing related class
        entityDraft1 = entityService.getEntityDraft(entityDto1.getId());
        DraftData draftData = DraftBuilder.forFieldEdit(entityDraft1.getField("newField").getId(), "metadata.0.value", entityDto3.getClassName());
        entityService.saveDraftEntityChanges(entityDto1.getId(), draftData);
        entityService.commitChanges(entityDto1.getId());
        //We changed related entity, so the old related entity field must be removed
        relatedField = getField(entityDto2.getId(), "relatedField");
        assertNull(relatedField);

        relatedField = getField(entityDto3.getId(), "relatedField");
        assertRelatedField(entityDto1, relatedField, "java.util.Set");

        entityDraft1 = entityService.getEntityDraft(entityDto1.getId());
        draftData = DraftBuilder.forFieldEdit(entityDraft1.getField("newField").getId(), "metadata.1.value", "java.util.List");
        entityService.saveDraftEntityChanges(entityDto1.getId(), draftData);
        draftData = DraftBuilder.forFieldEdit(entityDraft1.getField("newField").getId(), "metadata.2.value", "newNameForRelatedField");
        entityService.saveDraftEntityChanges(entityDto1.getId(), draftData);
        entityService.commitChanges(entityDto1.getId());
        relatedField = getField(entityDto3.getId(), "newNameForRelatedField");
        assertRelatedField(entityDto1, relatedField, "java.util.List");
    }

    @Test
    public void shouldAddLookupWithRelatedField() {
        EntityDto entityWithLookup = new EntityDto();
        entityWithLookup.setName("entityWithLookup");
        entityWithLookup = entityService.createEntity(entityWithLookup);
        EntityDto relatedEntity = new EntityDto();
        relatedEntity.setName("relatedEntity");
        relatedEntity = entityService.createEntity(relatedEntity);

        FieldDto stringField = FieldTestHelper.fieldDto("stringField", String.class);
        FieldDto nameField = FieldTestHelper.fieldDto("nameField", String.class);
        FieldDto lengthField = FieldTestHelper.fieldDto("lengthField", Long.class);
        FieldDto relationField = FieldTestHelper.fieldDto("relatedField", TypeDto.ONE_TO_MANY_RELATIONSHIP.getTypeClass());
        relationField.addMetadata(new MetadataDto(RELATED_CLASS, relatedEntity.getClassName()));

        entityService.addFields(entityWithLookup,  asList(stringField, relationField));
        entityService.addFields(relatedEntity, asList(nameField, lengthField));
        List<LookupFieldDto> lookupFieldDtos = lookupFieldsFromNames("relatedField.nameField", "relatedField.lengthField", "stringField");
        LookupDto lookup = new LookupDto("lookup", false, false, lookupFieldDtos, true);

        entityService.addLookups(entityWithLookup.getId(), lookup);

        LookupDto lookupFromDb = entityService.getLookupByName(entityWithLookup.getId(), "lookup");
        assertNotNull(lookupFromDb);
        assertEquals(asList("relatedField.nameField", "relatedField.lengthField", "stringField"), lookupFromDb.getFieldsOrder());
        assertEquals(asList("relatedField", "relatedField", "stringField"),
                extract(lookupFromDb.getLookupFields(), on(LookupFieldDto.class).getName()));
    }

    @Test
    public void shouldUpdateFetchDepth() {
        EntityDto entityDto = new EntityDto();
        entityDto.setName("FetchDepthTest");

        entityDto = entityService.createEntity(entityDto);
        assertNull(entityDto.getMaxFetchDepth());

        entityService.updateMaxFetchDepth(entityDto.getId(), 3);
        entityDto = entityService.getEntity(entityDto.getId());
        assertEquals(Integer.valueOf(3), entityDto.getMaxFetchDepth());
    }

    @Test
    public void shouldUpdateUserPreferencesAfterCommit() {
        EntityDto entityDto = new EntityDto();
        entityDto.setName("UserPrefTest");
        entityDto = entityService.createEntity(entityDto);

        final Long entityId = entityDto.getId();

        entityService.saveDraftEntityChanges(entityId,
                DraftBuilder.forNewField("disp", "f1name", Long.class.getName()));
        List<FieldDto> fields = entityService.getFields(entityId);
        assertNotNull(fields);
        assertEquals(asList("id", "creator", "owner", "modifiedBy", "creationDate", "modificationDate", "f1name"),
                extract(fields, on(FieldDto.class).getBasic().getName()));
        entityService.commitChanges(entityId);

        List<Field> draftFields = entityService.getEntityDraft(entityId).getFields();
        Field field = selectFirst(draftFields, having(on(Field.class).getName(), equalTo("f1name")));
        entityService.saveDraftEntityChanges(entityDto.getId(),
                DraftBuilder.forFieldEdit(field.getId(), "basic.name", "newName"));

        userPreferencesService.selectFields(entityId, "motech");
        userPreferencesService.unselectField(entityId, "motech", "id");
        userPreferencesService.unselectField(entityId, "motech", "owner");

        UserPreferencesDto userPreferencesDto = userPreferencesService.getUserPreferences(entityId, "motech");
        assertEquals(5, userPreferencesDto.getVisibleFields().size());
        assertTrue(userPreferencesDto.getVisibleFields().contains("f1name"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("creator"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("modifiedBy"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("creationDate"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("modificationDate"));

        entityService.commitChanges(entityId);

        userPreferencesDto = userPreferencesService.getUserPreferences(entityId, "motech");
        assertEquals(5, userPreferencesDto.getVisibleFields().size());
        assertTrue(userPreferencesDto.getVisibleFields().contains("newName"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("creator"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("modifiedBy"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("creationDate"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("modificationDate"));

        assertEquals(2, userPreferencesDto.getUnselectedFields().size());
        assertTrue(userPreferencesDto.getUnselectedFields().contains("id"));
        assertTrue(userPreferencesDto.getUnselectedFields().contains("owner"));
    }

    private void assertRelatedField(EntityDto relatedEntity, FieldDto relatedField, String collection) {
        assertNotNull(relatedField);
        assertEquals(relatedField.getMetadata(RELATED_FIELD).getValue(), "newField");
        assertEquals(relatedField.getMetadata(RELATIONSHIP_COLLECTION_TYPE).getValue(), collection);
        assertEquals(relatedField.getMetadata(RELATED_CLASS).getValue(), relatedEntity.getClassName());
        assertNull(relatedField.getMetadata(OWNING_SIDE));
    }

    private FieldDto getField(Long entityId, String fieldName) {
        List<FieldDto> fields = entityService.getEntityFields(entityId);
        FieldDto field = null;
        for (FieldDto f : fields) {
            if (f.getBasic().getName().equals(fieldName)) {
                field = f;
                break;
            }
        }
        return field;
    }

    private void setUpSecurityContext() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("mdsSchemaAccess");
        List<SimpleGrantedAuthority> authorities = asList(authority);

        User principal = new User("motech", "motech", authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "motech", authorities);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
