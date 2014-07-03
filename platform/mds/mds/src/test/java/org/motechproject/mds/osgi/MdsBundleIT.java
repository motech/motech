package org.motechproject.mds.osgi;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryExecutor;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.testutil.DraftBuilder;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.jdo.Query;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.mds.dto.SettingOptions.REQUIRE;
import static org.motechproject.mds.dto.TypeDto.BOOLEAN;
import static org.motechproject.mds.dto.TypeDto.LIST;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_KEY_TYPE;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_VALUE_TYPE;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MdsBundleIT extends BasePaxIT {

    private static final String FOO = "Foo";
    private static final String FOO_CLASS = String.format("%s.%s", Constants.PackagesGenerated.ENTITY, FOO);

    private static final int INSTANCE_COUNT = 5;
    private static final Byte[] BYTE_ARRAY_VALUE = new Byte[]{110, 111, 112};
    private static final Period TEST_PERIOD = new Period().withDays(3).withHours(7).withMinutes(50);
    private static final Period NEW_PERIOD = new Period().withYears(2).withMinutes(10);
    private static final Map<String, TestClass> TEST_MAP = new HashMap<>();
    private static final Map<String, TestClass> TEST_MAP2 = new HashMap<>();
    private static final DateTime NOW = DateUtil.now();
    private static final DateTime YEAR_LATER = NOW.plusYears(1);
    private static final LocalDate LD_NOW = DateUtil.now().toLocalDate();
    private static final LocalDate LD_YEAR_AGO = LD_NOW.minusYears(1);
    private static final Date DATE_NOW = DateUtil.now().toDate();
    private static final Date DATE_TOMORROW = DateUtil.now().plusDays(1).toDate();
    private static final Double DOUBLE_VALUE_1 = 4.725;
    private static final Double DOUBLE_VALUE_2 = 3.14;
    private static final Time MORNING_TIME = new Time(8, 15);
    private static final Time NIGHT_TIME = new Time(23, 4);

    static {
        TEST_MAP.put("key1", new TestClass(123, "abc"));
        TEST_MAP.put("key2", new TestClass(456, "ddd"));

        TEST_MAP2.put("key4", new TestClass(4, "ads"));
        TEST_MAP2.put("key3", new TestClass(21, "test"));
    }

    private JarGeneratorService generator;
    private EntityService entityService;
    private MotechDataService service;

    @Inject
    private BundleContext bundleContext;

    @Before
    public void setUp() throws Exception {
        WebApplicationContext context = ServiceRetriever.getWebAppContext(bundleContext, MDS_BUNDLE_SYMBOLIC_NAME, 10000, 12);

        entityService = context.getBean(EntityService.class);
        generator = context.getBean(JarGeneratorService.class);

        clearEntities();
        setUpSecurityContext();
    }

    @After
    public void tearDown() throws Exception {
        clearEntities();
    }

    @Test
    public void testEntitiesBundleInstallsProperly() throws Exception {
        final String serviceName = ClassName.getInterfaceName(FOO_CLASS);

        prepareTestEntities();

        Bundle entitiesBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, MDS_ENTITIES_SYMBOLIC_NAME);
        assertNotNull(entitiesBundle);

        service = (MotechDataService) ServiceRetriever.getService(bundleContext, serviceName);
        Class<?> objectClass = entitiesBundle.loadClass(FOO_CLASS);
        getLogger().info("Loaded class: " + objectClass.getName());

        clearInstances();

        verifyInstanceCreatingAndRetrieving(objectClass);
        verifyLookups();
        verifyComboboxValueUpdate();
        verifyInstanceUpdating();
        verifyCustomQuery();
        verifyColumnNameChange();
        verifyInstanceDeleting();
    }

    private void clearInstances() {
        service.deleteAll();
    }

    private void verifyInstanceCreatingAndRetrieving(Class<?> loadedClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        getLogger().info("Verifying instance creation and retrieval");

        Object instance = loadedClass.newInstance();
        Object instance2 = loadedClass.newInstance();
        Object instance3 = loadedClass.newInstance();
        Object instance4 = loadedClass.newInstance();
        Object instance5 = loadedClass.newInstance();

        updateInstance(instance, true, "trueNow", asList("1", "2", "3"),
                       NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1);
        updateInstance(instance2, true, "trueInRange", asList("2", "4"),
                       NOW.plusHours(1), LD_NOW.plusDays(1), TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 2);
        updateInstance(instance3, false, "falseInRange", null,
                       NOW.plusHours(1), LD_NOW.plusDays(1), null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 2);
        updateInstance(instance4, true, "trueOutOfRange", null,
                       NOW.plusHours(10), LD_NOW.plusDays(10), null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 3);
        updateInstance(instance5, true, "notInSet", null,
                       NOW, LD_NOW, null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_2, MORNING_TIME, 4);

        MethodUtils.invokeMethod(instance, "setSomeMap", TEST_MAP);

        //Single object return lookup should return 0 if there are no instances
        Long emptyCount = (Long) MethodUtils.invokeMethod(service, "countByUniqueString", "trueNow");
        assertEquals(emptyCount, (Long) 0L);

        service.create(instance);
        Object retrieved = service.retrieveAll().get(0);

        //Single object return lookup should return 1 if there is instance with unique value in field
        Long count = (Long) MethodUtils.invokeMethod(service, "countByUniqueString", "trueNow");
        assertEquals(count, (Long) 1L);

        assertInstance(retrieved, true, "trueNow", asList("1", "2", "3"),
                NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1);

        assertEquals(1, service.retrieveAll().size());
        service.create(instance2);
        service.create(instance3);
        service.create(instance4);
        service.create(instance5);
        assertEquals(INSTANCE_COUNT, service.retrieveAll().size());
    }


    private void verifyLookups() throws Exception{
        getLogger().info("Verifying lookups");

        Object resultObj = service.retrieveAll(QueryParams.ascOrder("someDateTime"));
        assertTrue(resultObj instanceof List);
        List resultList = (List) resultObj;

        assertEquals(5, resultList.size());
        assertInstance(resultList.get(0), true, "trueNow", asList("1", "2", "3"),
                       NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1);

        // verify lookups
        resultObj = MethodUtils.invokeMethod(service, "byBool",
                new Object[]{true, QueryParams.ascOrder("someDateTime")});

        assertTrue(resultObj instanceof List);
        resultList = (List) resultObj;

        assertEquals(4, resultList.size());
        assertInstance(resultList.get(0), true, "trueNow", asList("1", "2", "3"),
                       NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1);

        List<String> list = new ArrayList<>();
        list.add("2");

        // only two instances should match this criteria
        resultObj = MethodUtils.invokeMethod(service, "combined",
                new Object[]{true,
                        new Range<>(NOW.minusHours(1), NOW.plusHours(5)),
                        new HashSet<>(asList("trueNow", "trueInRange", "trueOutOfRange", "falseInRange")),
                        list,
                        QueryParams.descOrder("someDateTime")});

        assertTrue(resultObj instanceof List);
        resultList = (List) resultObj;
        assertEquals(2, resultList.size());
        assertInstance(resultList.get(0), true, "trueInRange", asList("2", "4"),
                       NOW.plusHours(1), LD_NOW.plusDays(1), TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 2);
        assertInstance(resultList.get(1), true, "trueNow", asList("1", "2", "3"),
                       NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1);

        // usage of a custom operator
        resultObj = MethodUtils.invokeMethod(service, "customOperator", 2);

        assertTrue(resultObj instanceof List);
        resultList = (List) resultObj;
        assertEquals(3, resultList.size());
        assertInstance(resultList.get(0), true, "trueNow", asList("1", "2", "3"),
                NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1);
        assertInstance(resultList.get(1), true, "trueInRange", asList("2", "4"),
                NOW.plusHours(1), LD_NOW.plusDays(1), TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 2);
        assertInstance(resultList.get(2), false, "falseInRange", Collections.emptyList(),
                NOW.plusHours(1), LD_NOW.plusDays(1), null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 2);

        // usage of matches
        resultObj = MethodUtils.invokeMethod(service, "matchesOperator", ".*true.*");

        assertTrue(resultObj instanceof List);
        resultList = (List) resultObj;
        assertEquals(3, resultList.size());
        assertInstance(resultList.get(0), true, "trueNow", asList("1", "2", "3"),
                NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1);
        assertInstance(resultList.get(1), true, "trueInRange", asList("2", "4"),
                NOW.plusHours(1), LD_NOW.plusDays(1), TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 2);
        updateInstance(resultList.get(2), true, "trueOutOfRange", null,
                NOW.plusHours(10), LD_NOW.plusDays(10), null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 3);
    }

    private void verifyInstanceUpdating() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        getLogger().info("Verifying instance updating");

        List<Object> allObjects = service.retrieveAll();
        assertEquals(allObjects.size(), INSTANCE_COUNT);

        Object retrieved = allObjects.get(0);

        updateInstance(retrieved, false, "anotherString", asList("4", "5"),
                       YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 10);

        service.update(retrieved);
        Object updated = service.retrieveAll().get(0);

        assertInstance(updated, false, "anotherString", asList("4", "5"),
                       YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 10);
    }

    private void verifyColumnNameChange() throws ClassNotFoundException, InterruptedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        getLogger().info("Verifying column name change");

        Long entityId = entityService.getEntityByClassName(FOO_CLASS).getId();
        List<Field> fieldsDto = entityService.getEntityDraft(entityId).getFields();

        entityService.saveDraftEntityChanges(entityId, DraftBuilder.forFieldEdit(fieldsDto.get(7).getId(), "basic.name", "newFieldName"));
        entityService.commitChanges(entityId);

        generator.regenerateMdsDataBundle(true);

        FieldDto updatedField = null;
        for (FieldDto field : entityService.getEntityFields(entityId)) {
            if ("newFieldName".equals(field.getBasic().getName())) {
                updatedField = field;
                break;
            }
        }
        assertNotNull("Unable to find field named 'newFieldName'", updatedField);
        assertEquals(String.class.getName(), updatedField.getType().getTypeClass());

        service = (MotechDataService) ServiceRetriever.getService(bundleContext, ClassName.getInterfaceName(FOO_CLASS), true);
        Object retrieved = service.retrieveAll().get(0);

        Object fieldValue = MethodUtils.invokeMethod(retrieved, "getNewFieldName", null);
        assertNotNull(fieldValue);

        entityId = entityService.getEntityByClassName(FOO_CLASS).getId();
        fieldsDto = entityService.getEntityDraft(entityId).getFields();

        entityService.saveDraftEntityChanges(entityId, DraftBuilder.forFieldEdit(fieldsDto.get(7).getId(), "basic.name", "someString"));
        entityService.commitChanges(entityId);
        generator.regenerateMdsDataBundle(true);

        service = (MotechDataService) ServiceRetriever.getService(bundleContext, ClassName.getInterfaceName(FOO_CLASS), true);
    }

    private void verifyCustomQuery() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List result = (List) service.executeQuery(new QueryExecution<List>() {
            @Override
            public List execute(Query query, InstanceSecurityRestriction restriction) {
                query.setFilter("someString == param0");
                query.declareParameters("java.lang.String param0");
                return (List) QueryExecutor.execute(query, "anotherString", restriction);
            }
        });
        assertEquals(1, result.size());
        assertInstance(result.get(0), false, "anotherString", asList("4", "5"),
                YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 10);
    }

    private void verifyInstanceDeleting() throws IllegalAccessException, InstantiationException {
        getLogger().info("Verifying instance deleting");

        List<Object> objects = service.retrieveAll();

        for (int i = 0; i < INSTANCE_COUNT; i++) {
            service.delete(objects.get(i));
            assertEquals(INSTANCE_COUNT - i - 1, service.retrieveAll().size());
        }
    }

    private void verifyComboboxValueUpdate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        getLogger().info("Verifying combobox value update");
        Long entityId = entityService.getEntityByClassName(FOO_CLASS).getId();

        List<Object> allObjects = service.retrieveAll();
        assertEquals(allObjects.size(), INSTANCE_COUNT);
        Object retrieved = allObjects.get(0);
        updateInstance(retrieved, false, "anotherString", asList("0", "35"),
                YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 3);
        service.update(retrieved);

        FieldDto comboboxField = entityService.getEntityFields(entityId).get(9);

        assertEquals("[1, 2, 3, 4, 0, 35]", comboboxField.getSetting(Constants.Settings.COMBOBOX_VALUES).getValue().toString());
    }

    private void prepareTestEntities() throws IOException {
        getLogger().info("Preparing entities for testing");

        EntityDto entityDto = new EntityDto(9999L, FOO);
        entityDto = entityService.createEntity(entityDto);
        generator.regenerateMdsDataBundle(true);

        List<FieldDto> fields = new ArrayList<>();
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.BOOLEAN,
                new FieldBasicDto("Some Boolean", "someBoolean"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.STRING,
                new FieldBasicDto("Some String", "someString"),
                false, null, null,
                asList(
                        new SettingDto("mds.form.label.textarea", false, BOOLEAN)
                ), null));
        fields.add(new FieldDto(null, entityDto.getId(),
                LIST,
                new FieldBasicDto("Some List", "someList"),
                false, null, null,
                asList(
                        new SettingDto(Constants.Settings.COMBOBOX_VALUES, new LinkedList<>(), LIST, REQUIRE),
                        new SettingDto(Constants.Settings.ALLOW_USER_SUPPLIED, true, BOOLEAN),
                        new SettingDto(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS, true, BOOLEAN)
                ), null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.DATETIME,
                new FieldBasicDto("dateTime", "someDateTime"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.MAP,
                new FieldBasicDto("someMap", "someMap"),
                false, Arrays.asList(
                        new MetadataDto(MAP_KEY_TYPE, String.class.getName()),
                        new MetadataDto(MAP_VALUE_TYPE, TestClass.class.getName())),
                null, null,null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.PERIOD,
                new FieldBasicDto("somePeriod", "somePeriod"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.BLOB,
                new FieldBasicDto("someBlob", "someBlob"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.LOCAL_DATE,
                new FieldBasicDto("someLocalDate", "someLocalDate"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.DATE,
                new FieldBasicDto("someDate", "someDate"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.TIME,
                new FieldBasicDto("someTime", "someTime"),
                false, null));

        List<SettingDto> decimalSettings = asList(new SettingDto("mds.form.label.precision", 10),
                new SettingDto("mds.form.label.scale", 5));

        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.DOUBLE,
                new FieldBasicDto("Some Decimal", "someDecimal"),
                false, null, null, decimalSettings, null));

        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.INTEGER,
                new FieldBasicDto("someInteger", "someInt"),
                false, null));

        entityService.addFields(entityDto, fields);

        List<LookupDto> lookups = new ArrayList<>();
        List<LookupFieldDto> lookupFields = new ArrayList<>();

        lookupFields.add(new LookupFieldDto(null, "someBoolean", LookupFieldDto.Type.VALUE));
        lookups.add(new LookupDto("By boolean", false, false, lookupFields, true, "byBool"));

        lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldDto(null, "someString", LookupFieldDto.Type.VALUE));
        lookups.add(new LookupDto("By unique String", true, false, lookupFields, true, "byUniqueString"));

        lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldDto(null, "someBoolean", LookupFieldDto.Type.VALUE));
        lookupFields.add(new LookupFieldDto(null, "someDateTime", LookupFieldDto.Type.RANGE));
        lookupFields.add(new LookupFieldDto(null, "someString", LookupFieldDto.Type.SET));
        lookupFields.add(new LookupFieldDto(null, "someList", LookupFieldDto.Type.VALUE));
        lookups.add(new LookupDto("Combined", false, false, lookupFields, true));

        lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldDto(null, "someInt", LookupFieldDto.Type.VALUE, "<="));
        lookups.add(new LookupDto("With custom operator", false, false, lookupFields, true, "customOperator"));

        lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldDto(null, "someString", LookupFieldDto.Type.VALUE, "matches()"));
        lookups.add(new LookupDto("With matches", false, false, lookupFields, true, "matchesOperator"));

        entityService.addLookups(entityDto.getId(), lookups);

        entityService.commitChanges(entityDto.getId());
        generator.regenerateMdsDataBundle(true);

        getLogger().info("Entities ready for testing");
    }

    private void setUpSecurityContext() {
        getLogger().info("Setting up security context");

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("mdsSchemaAccess");
        List<SimpleGrantedAuthority> authorities = asList(authority);

        User principal = new User("motech", "motech", authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null);
        authentication.setAuthenticated(false);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    private void clearEntities() {
        getLogger().info("Cleaning up entities");

        for (EntityDto entity : entityService.listEntities()) {
            entityService.deleteEntity(entity.getId());
        }
    }

    private void updateInstance(Object instance, Boolean boolField, String stringField, List listField,
                                DateTime dateTimeField, LocalDate localDateField, Map map, Period period,
                                Byte[] blob, Date dateField, Double decimalField, Time timeField, Integer intField)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PropertyUtils.setProperty(instance, "someBoolean", boolField);
        PropertyUtils.setProperty(instance, "someString", stringField);
        PropertyUtils.setProperty(instance, "someList", listField);
        PropertyUtils.setProperty(instance, "someDateTime", dateTimeField);
        PropertyUtils.setProperty(instance, "someLocalDate", localDateField);
        PropertyUtils.setProperty(instance, "someMap", map);
        PropertyUtils.setProperty(instance, "somePeriod", period);
        PropertyUtils.setProperty(instance, "someBlob", blob);
        PropertyUtils.setProperty(instance, "someDate", dateField);
        PropertyUtils.setProperty(instance, "someDecimal", decimalField);
        PropertyUtils.setProperty(instance, "someTime", timeField);
        PropertyUtils.setProperty(instance, "someInt", intField);
    }

    private void assertInstance(Object instance, Boolean boolField, String stringField, List listField,
                                DateTime dateTimeField, LocalDate localDateField, Map map, Period period,
                                Byte[] blob, Date dateField, Double decimalField, Time timeField, Integer intField)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        assertNotNull(instance);
        assertEquals(boolField, PropertyUtils.getProperty(instance, "someBoolean"));
        assertEquals(stringField, PropertyUtils.getProperty(instance, "someString"));
        assertEquals(listField, PropertyUtils.getProperty(instance, "someList"));
        assertEquals(dateTimeField, PropertyUtils.getProperty(instance, "someDateTime"));
        assertEquals(map, PropertyUtils.getProperty(instance, "someMap"));
        assertEquals(period, PropertyUtils.getProperty(instance, "somePeriod"));
        assertEquals(localDateField, PropertyUtils.getProperty(instance, "someLocalDate"));
        assertEquals(dateField, PropertyUtils.getProperty(instance, "someDate"));
        assertEquals(decimalField, PropertyUtils.getProperty(instance, "someDecimal"));
        assertEquals(timeField, PropertyUtils.getProperty(instance, "someTime"));
        assertEquals(intField, PropertyUtils.getProperty(instance, "someInt"));

        // assert blob
        Object blobValue = service.getDetachedField(instance, "someBlob");
        assertEquals(Arrays.toString(blob), Arrays.toString((Byte[]) blobValue));
    }


}
