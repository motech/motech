package org.motechproject.mds.it.osgi;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.commons.sql.service.SqlDBManager;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.dto.CsvImportResults;
import org.motechproject.mds.dto.DraftData;
import org.motechproject.mds.dto.DtoHelper;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.osgi.TestClass;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryExecutor;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.query.SqlQueryExecution;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MDSLookupService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.RestDocumentationService;
import org.motechproject.mds.testutil.DraftBuilder;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.jdo.Query;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.motechproject.mds.dto.SettingOptions.REQUIRE;
import static org.motechproject.mds.dto.TypeDto.BOOLEAN;
import static org.motechproject.mds.dto.TypeDto.COLLECTION;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_KEY_TYPE;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_VALUE_TYPE;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
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
    private static final DateTime NOW = DateUtil.now().secondOfMinute().roundFloorCopy();
    private static final DateTime YEAR_LATER = NOW.plusYears(1);
    private static final LocalDate LD_NOW = NOW.toLocalDate();
    private static final LocalDate LD_YEAR_AGO = LD_NOW.minusYears(1);
    private static final Date DATE_NOW = NOW.toDate();
    private static final Date DATE_TOMORROW = NOW.plusDays(1).toDate();
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
    private SqlDBManager sqlDBManager;

    @Inject
    private BundleContext bundleContext;

    @Inject
    private MDSLookupService mdsLookupService;

    @Inject
    private RestDocumentationService restDocService;

    @Before
    public void setUp() throws Exception {
        WebApplicationContext context = ServiceRetriever.getWebAppContext(bundleContext, MDS_BUNDLE_SYMBOLIC_NAME, 10000, 12);

        entityService = context.getBean(EntityService.class);
        generator = context.getBean(JarGeneratorService.class);

        clearEntities();
        setUpSecurityContextForDefaultUser("mdsSchemaAccess");
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
        verifyInstanceCreatingOrUpdating(objectClass);
        verifyLookups(false); // regular lookups
        verifyLookups(true); // using the lookup service
        verifyComboboxValueUpdate();
        verifyInstanceUpdating();
        verifyCustomQuery();
        verifyCsvImport();
        verifyCsvImportIsOneTransaction();
        verifyColumnNameChange();
        verifyComboboxDataMigration();
        verifyInstanceDeleting();
        verifyRestDocumentation();
    }

    private void verifyComboboxDataMigration() throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Long entityId = entityService.getEntityByClassName(FOO_CLASS).getId();
        Long fieldId = getFieldIdByName(entityService.getFields(entityId), "someEnum");

        DraftData draft = DraftBuilder.forFieldEdit(fieldId, "settings.2.value", true);

        entityService.saveDraftEntityChanges(entityId, draft);
        entityService.commitChanges(entityId);

        generator.regenerateMdsDataBundle(true);
        service = (MotechDataService) ServiceRetriever.getService(bundleContext, ClassName.getInterfaceName(FOO_CLASS), true);

        assertValuesEqual(getExpectedComboboxValues(), getValues(service.retrieveAll()));
    }

    private void assertValuesEqual(List<List<Object>> expected, List<List<Object>> result) {
        boolean equal = true;

        for (int i = 0; i < expected.size(); i++) {
            if (!expected.get(i).isEmpty()) {
                if (!expected.get(i).get(0).toString().equals(result.get(i).get(0).toString())) {
                    equal = false;
                    break;
                }
            } else {
                if (!result.get(i).isEmpty()) {
                    equal = false;
                    break;
                }
            }
        }

        assertTrue(equal);
    }

    private List<List<Object>> getExpectedComboboxValues() throws ClassNotFoundException, NoSuchFieldException {
        Class aClass = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, MDS_ENTITIES_SYMBOLIC_NAME).loadClass(FOO_CLASS);
        List<List<Object>> values = new ArrayList<>();

        List<Object> value = new ArrayList<>();
        value.add(toEnum(aClass, "two"));
        values.add(value);
        values.add(value);

        value = new ArrayList<>();
        value.add(toEnum(aClass, "three"));
        values.add(value);

        value = new ArrayList<>();
        value.add(toEnum(aClass, "one"));
        values.add(value);

        value = new ArrayList<>();
        value.add(toEnum(aClass, "two"));
        values.add(value);

        value = new ArrayList<>();
        values.add(value);
        values.add(value);

        return values;
    }

    private List<List<Object>> getValues(List instances) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<List<Object>> values = new ArrayList<>();
        for (Object instance : instances) {

            values.add((List<Object>)instance.getClass().getMethod("getSomeEnum").invoke(instance));
        }
        return values;
    }

    private Long getFieldIdByName(List<FieldDto> fields, String name) {
        Long fieldId = null;

        for (FieldDto field : fields) {
            if(field.getBasic().getName().equals(name)) {
                fieldId = field.getId();
                break;
            }
        }

        return  fieldId;
    }

    private void clearInstances() {
        service.deleteAll();
    }

    private void verifyInstanceCreatingAndRetrieving(Class<?> loadedClass) throws Exception {
        getLogger().info("Verifying instance creation and retrieval");

        Object instance = loadedClass.newInstance();
        Object instance2 = loadedClass.newInstance();
        Object instance3 = loadedClass.newInstance();
        Object instance4 = loadedClass.newInstance();
        Object instance5 = loadedClass.newInstance();

        updateInstance(instance, true, "trueNow", "trueNowCp", new ArrayList(asList("1", "2", "3")),
                       NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1, toEnum(loadedClass, "one"));
        updateInstance(instance2, true, "trueInRange", "trueInRangeCp", new ArrayList(asList("2", "4")),
                       NOW.plusHours(1), LD_NOW.plusDays(1), TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 2, toEnum(loadedClass, "two"));
        updateInstance(instance3, false, "falseInRange", "falseInRangeCp", null,
                       NOW.plusHours(2), LD_NOW.plusDays(1), null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 2, toEnum(loadedClass, "three"));
        updateInstance(instance4, true, "trueOutOfRange", "trueOutOfRangeCp", null,
                       NOW.plusHours(3), LD_NOW.plusDays(10), null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 3, toEnum(loadedClass, "one"));
        updateInstance(instance5, true, "notInSet", "notInSetCp", null,
                       NOW.plusHours(4), LD_NOW, null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_2, MORNING_TIME, 4, toEnum(loadedClass, "two"));

        MethodUtils.invokeMethod(instance, "setSomeMap", TEST_MAP);

        //Single object return lookup should return 0 if there are no instances
        Long emptyCount = (Long) MethodUtils.invokeMethod(service, "countByUniqueString", "trueNow");
        assertEquals(emptyCount, (Long) 0L);

        service.create(instance);
        Object retrieved = service.retrieveAll(QueryParams.ascOrder("someDateTime")).get(0);

        //Single object return lookup should return 1 if there is instance with unique value in field
        Long count = (Long) MethodUtils.invokeMethod(service, "countByUniqueString", "trueNow");
        assertEquals(count, (Long) 1L);

        assertInstance(retrieved, true, "trueNow", "trueNowCp", asList("1", "2", "3"),
                NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1, toEnum(loadedClass, "one"));

        assertEquals(1, service.retrieveAll().size());
        service.create(instance2);
        service.create(instance3);
        service.create(instance4);
        service.create(instance5);
        assertEquals(INSTANCE_COUNT, service.retrieveAll().size());
    }

    private void verifyLookups(boolean usingLookupService) throws Exception{
        // if using lookup service set tot true then all data access
        // is done through the MdsLookupService
        // otherwise we call the methods on the data service

        if (usingLookupService) {
            getLogger().info("Verifying lookups using lookup service");
        } else {
            getLogger().info("Verifying lookups");
        }

        Object resultObj = (usingLookupService) ?
                mdsLookupService.retrieveAll(FOO_CLASS, QueryParams.ascOrder("someDateTime")) :
                service.retrieveAll(QueryParams.ascOrder("someDateTime"));

        assertTrue(resultObj instanceof List);
        List resultList = (List) resultObj;
        assertEquals(5, resultList.size());

        Class objClass = resultList.get(0).getClass();

        assertInstance(resultList.get(0), true, "trueNow", "trueNowCp", asList("1", "2", "3"),
                       NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1, toEnum(objClass, "one"));

        // verify lookups
        if (usingLookupService) {
            Map<String, Boolean> lookupMap = new HashMap<>();
            lookupMap.put("someBoolean", true);
            resultObj = mdsLookupService.findMany(FOO_CLASS, "By boolean",
                    lookupMap, QueryParams.ascOrder("someDateTime"));
        } else {
            resultObj = MethodUtils.invokeMethod(service, "byBool",
                    new Object[]{true, QueryParams.ascOrder("someDateTime")});
        }

        assertTrue(resultObj instanceof List);
        resultList = (List) resultObj;

        assertEquals(4, resultList.size());
        assertInstance(resultList.get(0), true, "trueNow", "trueNowCp", asList("1", "2", "3"),
                       NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1, toEnum(objClass, "one"));

        List<String> list = new ArrayList<>();
        list.add("2");
        Range<DateTime> range = new Range<>(NOW.minusHours(1), NOW.plusHours(5));
        Set<String> set = new HashSet<>(asList("trueNow", "trueInRange", "trueOutOfRange", "falseInRange"));

        // only two instances should match this criteria
        if (usingLookupService) {
            Map<String, Object> lookupMap = new HashMap<>();
            lookupMap.put("someBoolean", true);
            lookupMap.put("someDateTime", range);
            lookupMap.put("someString", set);
            lookupMap.put("someList", list);

            resultObj = mdsLookupService.findMany(FOO_CLASS, "Combined", lookupMap, QueryParams.descOrder("someDateTime"));
        } else {
            resultObj = MethodUtils.invokeMethod(service, "combined",
                    new Object[]{true, range, set, list, QueryParams.descOrder("someDateTime")});
        }

        assertTrue(resultObj instanceof List);
        resultList = (List) resultObj;
        assertEquals(2, resultList.size());
        assertInstance(resultList.get(0), true, "trueInRange", "trueInRangeCp", asList("2", "4"),
                       NOW.plusHours(1), LD_NOW.plusDays(1), TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 2, toEnum(objClass, "two"));
        assertInstance(resultList.get(1), true, "trueNow", "trueNowCp", asList("1", "2", "3"),
                       NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1, toEnum(objClass, "one"));

        // usage of a custom operator
        if (usingLookupService) {
            Map<String, Integer> lookupMap = new HashMap<>();
            lookupMap.put("someInt", 2);
            resultObj = mdsLookupService.findMany(FOO_CLASS, "With custom operator", lookupMap,
                    QueryParams.ascOrder("someDateTime"));
        } else {
            resultObj = MethodUtils.invokeMethod(service, "customOperator",
                    new Object[]{2, QueryParams.ascOrder("someDateTime")});
        }

        assertTrue(resultObj instanceof List);
        resultList = (List) resultObj;
        assertEquals(3, resultList.size());
        assertInstance(resultList.get(0), true, "trueNow", "trueNowCp", asList("1", "2", "3"),
                NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1, toEnum(objClass, "one"));
        assertInstance(resultList.get(1), true, "trueInRange", "trueInRangeCp", asList("2", "4"),
                NOW.plusHours(1), LD_NOW.plusDays(1), TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 2, toEnum(objClass, "two"));
        assertInstance(resultList.get(2), false, "falseInRange", "falseInRangeCp", Collections.emptyList(),
                NOW.plusHours(2), LD_NOW.plusDays(1), null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 2, toEnum(objClass, "three"));

        // usage of matches, case sensitive and case insensitive
        String[] textsToSearch = { "true", "TRUE" };
        String[] methodNames = {"matchesOperator", "matchesOperatorCI"};
        String[] lookupNames = {"With matches", "With matches case insensitive"};

        for (int i = 0; i < 2; i++) {
            String textToSearch = textsToSearch[i];
            String methodName = methodNames[i];
            String lookupName = lookupNames[i];

            if (usingLookupService) {
                Map<String, String> lookupMap = new HashMap<>();
                lookupMap.put("someString", textToSearch);
                resultObj = mdsLookupService.findMany(FOO_CLASS, lookupName, lookupMap, QueryParams.ascOrder("someDateTime"));
            } else {
                resultObj = MethodUtils.invokeMethod(service, methodName,
                        new Object[]{textToSearch, QueryParams.ascOrder("someDateTime")});
            }

            assertTrue(resultObj instanceof List);
            resultList = (List) resultObj;
            assertEquals(3, resultList.size());
            assertInstance(resultList.get(0), true, "trueNow", "trueNowCp", asList("1", "2", "3"),
                    NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                    DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1, toEnum(objClass, "one"));
            assertInstance(resultList.get(1), true, "trueInRange", "trueInRangeCp", asList("2", "4"),
                    NOW.plusHours(1), LD_NOW.plusDays(1), TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                    DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 2, toEnum(objClass, "two"));
            updateInstance(resultList.get(2), true, "trueOutOfRange", "trueOutOfRangeCp", null,
                    NOW.plusHours(3), LD_NOW.plusDays(10), null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                    DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 3, toEnum(objClass, "one"));
        }
    }

    private void verifyInstanceUpdating() throws Exception {
        getLogger().info("Verifying instance updating");

        List<Object> allObjects = service.retrieveAll(QueryParams.descOrder("someDateTime"));
        assertEquals(allObjects.size(), INSTANCE_COUNT);

        Object retrieved = allObjects.get(0);
        Class objClass = retrieved.getClass();

        updateInstance(retrieved, false, "anotherString", "anotherStringCp", new ArrayList(asList("4", "5")),
                YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 10, toEnum(objClass, "two"));

        service.update(retrieved);
        Object updated = service.retrieveAll(QueryParams.descOrder("someDateTime")).get(0);

        assertInstance(updated, false, "anotherString", "anotherStringCp", asList("4", "5"),
                       YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 10, toEnum(objClass, "two"));
    }

    private void verifyInstanceCreatingOrUpdating(Class<?> loadedClass) throws Exception {
        getLogger().info("Verifying instance creating or updating using createOrUpdate() method");

        // Creating a new object using createOrUpdate() method and checking if it was really added
        Object instance = loadedClass.newInstance();

        updateInstance(instance, false, "newInstance", "newInstance", new ArrayList(asList("1", "2", "3")),
                NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1, toEnum(loadedClass, "one"));

        service.createOrUpdate(instance);                           // using createOrUpdate() to create

        List<Object> allObjects = service.retrieveAll();
        assertEquals(allObjects.size(), INSTANCE_COUNT + 1);        // should return one extra object


        // Now update that object using createOrUpdate method and check if it is really updated
        Object retrieved = allObjects.get(INSTANCE_COUNT);          // gets the last added object
        Class objClass = retrieved.getClass();

        updateInstance(retrieved, false, "yetAnotherString", "yetAnotherStringCp", new ArrayList(asList("1", "2", "3")),
                YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 10, toEnum(objClass, "two"));

        service.createOrUpdate(retrieved);                          // using createOrUpdate() to update

        assertEquals(allObjects.size(), INSTANCE_COUNT + 1);        // number of objects shouldn't change since last check

        Object updated = service.retrieveAll().get(INSTANCE_COUNT); // gets the last added object

        assertInstance(updated, false, "yetAnotherString", "yetAnotherStringCp", asList("1", "2", "3"),
                YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 10, toEnum(objClass, "two"));

        // Remove new object for the sake of other tests
        service.delete(updated);
        allObjects = service.retrieveAll();
        assertEquals(allObjects.size(), INSTANCE_COUNT);            // check if the object is removed
    }

    private void verifyColumnNameChange() throws ClassNotFoundException, InterruptedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        getLogger().info("Verifying column name change");

        Long entityId = entityService.getEntityByClassName(FOO_CLASS).getId();
        List<Field> fields = entityService.getEntityDraft(entityId).getFields();

        Field fieldToUpdate  = findFieldByName(fields, "someString");

        entityService.saveDraftEntityChanges(entityId, DraftBuilder.forFieldEdit(fieldToUpdate.getId(), "basic.name", "newFieldName"));
        entityService.commitChanges(entityId);

        generator.regenerateMdsDataBundle();

        FieldDto fieldToUpdateDto = DtoHelper.findByName(entityService.getEntityFields(entityId), "newFieldName");

        assertNotNull("Unable to find field named 'newFieldName'", fieldToUpdate);
        assertEquals(String.class.getName(), fieldToUpdateDto.getType().getTypeClass());

        service = (MotechDataService) ServiceRetriever.getService(bundleContext, ClassName.getInterfaceName(FOO_CLASS), true);
        Object retrieved = service.retrieveAll(QueryParams.ascOrder("someDateTime")).get(0);

        Object fieldValue = MethodUtils.invokeMethod(retrieved, "getNewFieldName", null);
        assertNotNull(fieldValue);

        entityId = entityService.getEntityByClassName(FOO_CLASS).getId();
        fields = entityService.getEntityDraft(entityId).getFields();

        fieldToUpdate = findFieldByName(fields, "newFieldName");

        entityService.saveDraftEntityChanges(entityId, DraftBuilder.forFieldEdit(fieldToUpdate.getId(), "basic.name", "someString"));
        entityService.commitChanges(entityId);
        generator.regenerateMdsDataBundle();

        service = (MotechDataService) ServiceRetriever.getService(bundleContext, ClassName.getInterfaceName(FOO_CLASS), true);
    }

    private void verifyCustomQuery() throws Exception {
        List result = (List) service.executeQuery(new QueryExecution<List>() {
            @Override
            public List execute(Query query, InstanceSecurityRestriction restriction) {
                query.setFilter("someString == param0");
                query.declareParameters("java.lang.String param0");
                return (List) QueryExecutor.execute(query, "anotherString", restriction);
            }
        });
        assertEquals(1, result.size());

        Class objClass = result.get(0).getClass();
        assertInstance(result.get(0), false, "anotherString", "anotherStringCp", asList("4", "5"),
                YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 10, toEnum(objClass, "two"));

        List<String> names = (List<String>) service.executeSQLQuery(new SqlQueryExecution<List<String>>() {
            @Override
            public List<String> execute(Query query) {
                Map<String, Integer> params = new HashMap<>();
                params.put("param", 4);
                return (List<String>) query.executeWithMap(params);
            }

            @Override
            public String getSqlQuery() {
                String driverName = sqlDBManager.getChosenSQLDriver();
                if (driverName.equals(Constants.Config.MYSQL_DRIVER_CLASSNAME)) {
                    return "SELECT someString FROM MDS_FOO WHERE someInt = :param";
                } else {
                    return "SELECT \"someString\" FROM \"MDS_FOO\" WHERE \"someInt\" = :param";
                }
            }
        });
        assertEquals(asList("notInSet"), names);
    }

    private void verifyInstanceDeleting() throws IllegalAccessException, InstantiationException {
        getLogger().info("Verifying instance deleting");

        // 2 instances come from csv
        int instanceCount = INSTANCE_COUNT + 2;

        List<Object> objects = service.retrieveAll();

        for (int i = 0; i < instanceCount; i++) {
            service.delete(objects.get(i));
            assertEquals(instanceCount - i - 1, service.retrieveAll().size());
        }
    }

    private void verifyComboboxValueUpdate() throws Exception {
        getLogger().info("Verifying combobox value update");
        Long entityId = entityService.getEntityByClassName(FOO_CLASS).getId();

        List<Object> allObjects = service.retrieveAll(QueryParams.ascOrder("someDateTime"));
        assertEquals(allObjects.size(), INSTANCE_COUNT);
        Object retrieved = allObjects.get(0);
        Class objClass = retrieved.getClass();

        updateInstance(retrieved, false, "anotherString", "anotherStringCp", new ArrayList(asList("0", "35")),
                YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 3, toEnum(objClass, "two"));
        service.update(retrieved);

        FieldDto comboboxField = entityService.findEntityFieldByName(entityId, "someList");

        // If this test fails be sure to check if any unexpected values were added to comboboxField earlier.
        // At the moment all values remain in comboboxField, even after the object instances that added them were deleted.
        assertEquals("[1, 2, 3, 4, 0, 35]", comboboxField.getSetting(Constants.Settings.COMBOBOX_VALUES).getValue().toString());
    }

    private void verifyCsvImport() throws Exception {
        getLogger().info("Verifying CSV Import");

        CsvImportExportService csvImportExportService = ServiceRetriever.getService(bundleContext, CsvImportExportService.class);

        try (InputStream in = new ClassPathResource("csv/import.csv").getInputStream()) {
            Reader reader = new InputStreamReader(in);
            CsvImportResults results = csvImportExportService.importCsv(FOO_CLASS, reader, "import.csv", false);
            assertNotNull(results);
            assertEquals(2, results.totalNumberOfImportedInstances());
            assertEquals(2, results.newInstanceCount());
            assertEquals(0, results.updatedInstanceCount());
        }

        assertEquals(7, service.count());

        // get the imported instances through a lookup
        QueryParams queryParams = new QueryParams(new Order("someTime", Order.Direction.DESC));
        List list = (List) MethodUtils.invokeExactMethod(service, "matchesOperator",
                new Object[] {"fromCsv", queryParams});

        assertNotNull(list);
        assertEquals(2, list.size());
        assertInstance(list.get(0), false, "fromCsv", "Capital CSV", Collections.emptyList(), null, new LocalDate(2012, 10, 14),
                null, new Period(2, 0, 0, 0, 0, 0, 0, 0), null, new DateTime(2014, 12, 2, 16, 13, 40, 120, DateTimeZone.UTC).toDate(),
                null, new Time(20, 20), null, null);
        assertInstance(list.get(1), true, "fromCsv", "Capital CSV", new ArrayList(asList("one", "two")),
                new DateTime(2014, 12, 2, 13, 10, 40, 120, DateTimeZone.UTC).withZone(DateTimeZone.getDefault()),
                new LocalDate(2012, 10, 15), null, new Period(1, 0, 0, 0, 0, 0, 0, 0), null,
                new DateTime(2014, 12, 2, 13, 13, 40, 120, DateTimeZone.UTC).toDate(), null, new Time(10, 30), null, null);
    }

    private void verifyCsvImportIsOneTransaction() throws Exception {
        getLogger().info("Verifying that CSV Import is done in one transaction");

        CsvImportExportService csvImportExportService = ServiceRetriever.getService(bundleContext, CsvImportExportService.class);

        boolean exceptionThrown = false;
        try (InputStream in = new ClassPathResource("csv/import.csv").getInputStream()) {
            String csv = IOUtils.toString(in);
            csv += "invalid row";
            csvImportExportService.importCsv(FOO_CLASS, new StringReader(csv), "import.csv", false);
        } catch (RuntimeException e) {
            exceptionThrown = true;
        }

        assertTrue("No exception thrown during invalid CSV import", exceptionThrown);

        // verify that there are no new instances
        assertEquals(7, service.count());
    }

    private void verifyRestDocumentation() {
        StringWriter writer = new StringWriter();
        restDocService.retrieveDocumentation(writer, "/motech-platform-server", new Locale("en", "US"));
        String docs = writer.toString();

        // Verification of generation in done in SwaggerGeneratorTest
        // Here we just check if generation took place. We don't want to export swagger packages.

        assertNotNull(docs);
        assertNotSame("", docs);
    }

    private void prepareTestEntities() throws IOException {
        getLogger().info("Preparing entities for testing");

        EntityDto entityDto = new EntityDto(9999L, FOO);
        entityDto = entityService.createEntity(entityDto);
        generator.regenerateMdsDataBundle();

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
        // test with capitalized name
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.STRING,
                new FieldBasicDto("Capital Name", "CapitalName"),
                false, null, null,
                asList(
                        new SettingDto("mds.form.label.textarea", false, BOOLEAN)
                ), null));
        fields.add(new FieldDto(null, entityDto.getId(),
                COLLECTION,
                new FieldBasicDto("Some List", "someList"),
                false, null, null,
                asList(
                        new SettingDto(Constants.Settings.COMBOBOX_VALUES, new LinkedList<>(), COLLECTION, REQUIRE),
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

        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.COLLECTION,
                new FieldBasicDto("Some Enum", "someEnum"),
                false, null, null,
                asList(
                        new SettingDto(Constants.Settings.COMBOBOX_VALUES, asList("one", "two", "three"), COLLECTION, REQUIRE),
                        new SettingDto(Constants.Settings.ALLOW_USER_SUPPLIED, false, BOOLEAN),
                        new SettingDto(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS, false, BOOLEAN)
                ), null));

        entityService.addFields(entityDto, fields);

        List<LookupDto> lookups = new ArrayList<>();
        List<LookupFieldDto> lookupFields = new ArrayList<>();

        lookupFields.add(new LookupFieldDto(null, "someBoolean", LookupFieldType.VALUE));
        lookups.add(new LookupDto("By boolean", false, false, lookupFields, true, "byBool", asList("someBoolean")));

        lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldDto(null, "someString", LookupFieldType.VALUE));
        lookups.add(new LookupDto("By unique String", true, false, lookupFields, true, "byUniqueString", asList("someString")));

        lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldDto(null, "someBoolean", LookupFieldType.VALUE));
        lookupFields.add(new LookupFieldDto(null, "someDateTime", LookupFieldType.RANGE));
        lookupFields.add(new LookupFieldDto(null, "someString", LookupFieldType.SET));
        lookupFields.add(new LookupFieldDto(null, "someList", LookupFieldType.VALUE));
        lookups.add(new LookupDto("Combined", false, false, lookupFields, true));

        lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldDto(null, "someInt", LookupFieldType.VALUE, "<="));
        lookups.add(new LookupDto("With custom operator", false, false, lookupFields, true, "customOperator", asList("someInt")));

        lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldDto(null, "someString", LookupFieldType.VALUE, "matches()"));
        lookups.add(new LookupDto("With matches", false, false, lookupFields, true, "matchesOperator", asList("someString")));

        lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldDto(null, "someString", LookupFieldType.VALUE,
                Constants.Operators.MATCHES_CASE_INSENSITIVE));
        lookups.add(new LookupDto("With matches case insensitive", false, false, lookupFields, true, "matchesOperatorCI",
                singletonList("someString")));

        entityService.addLookups(entityDto.getId(), lookups);
        entityService.commitChanges(entityDto.getId());
        generator.regenerateMdsDataBundle();

        getLogger().info("Entities ready for testing");
    }

    private void clearEntities() {
        getLogger().info("Cleaning up entities");

        for (EntityDto entity : entityService.listEntities()) {
            if (!entity.isDDE()) {
                entityService.deleteEntity(entity.getId());
            }
        }
    }

    private void updateInstance(Object instance, Boolean boolField, String stringField, String capitalizedStrField, List listField,
                                DateTime dateTimeField, LocalDate localDateField, Map map, Period period,
                                Byte[] blob, Date dateField, Double decimalField, Time timeField, Integer intField,
                                Object enumVal)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PropertyUtils.setProperty(instance, "someBoolean", boolField);
        PropertyUtils.setProperty(instance, "someString", stringField);
        PropertyUtil.safeSetProperty(instance, "CapitalName", capitalizedStrField);
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
        PropertyUtils.setProperty(instance, "someEnum", enumVal);
    }

    private void assertInstance(Object instance, Boolean boolField, String stringField, String capitalizedStrField, List listField,
                                DateTime dateTimeField, LocalDate localDateField, Map map, Period period,
                                Byte[] blob, Date dateField, Double decimalField, Time timeField, Integer intField,
                                Object enumVal)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        assertNotNull(instance);
        assertEquals(boolField, PropertyUtils.getProperty(instance, "someBoolean"));
        assertEquals(stringField, PropertyUtils.getProperty(instance, "someString"));
        assertEquals(capitalizedStrField, PropertyUtil.safeGetProperty(instance, "CapitalName"));
        assertEquals(listField, PropertyUtils.getProperty(instance, "someList"));
        assertEquals(dateTimeField, PropertyUtils.getProperty(instance, "someDateTime"));
        assertEquals(map, PropertyUtils.getProperty(instance, "someMap"));
        assertEquals(period, PropertyUtils.getProperty(instance, "somePeriod"));
        assertEquals(localDateField, PropertyUtils.getProperty(instance, "someLocalDate"));
        assertEquals(dateField, PropertyUtils.getProperty(instance, "someDate"));
        assertEquals(decimalField, PropertyUtils.getProperty(instance, "someDecimal"));
        assertEquals(timeField, PropertyUtils.getProperty(instance, "someTime"));
        assertEquals(intField, PropertyUtils.getProperty(instance, "someInt"));
        assertEquals(enumVal, PropertyUtils.getProperty(instance, "someEnum"));

        // assert blob
        Object blobValue = service.getDetachedField(instance, "someBlob");
        assertEquals(Arrays.toString(blob), Arrays.toString((Byte[]) blobValue));
    }

    private Object toEnum(Class entityClass, String str) throws NoSuchFieldException {
        Class enumClass;
        java.lang.reflect.Field someEnumField = entityClass.getDeclaredField("someEnum");
        if (someEnumField.getType().isEnum()) {
            enumClass = someEnumField.getType();
        } else {
            ParameterizedType type = (ParameterizedType) someEnumField.getGenericType();
            enumClass = (Class)type.getActualTypeArguments()[0];
        }
        Object[] constants = enumClass.getEnumConstants();
        for (Object constant : constants) {
            if (constant.toString().equals(str)) {
                return constant;
            }
        }

        fail("Value [" + str + "] not found in enum " + enumClass.getName());

        return null;
    }

    private Field findFieldByName(Collection<Field> fields, String name) {
        for (Field field : fields) {
            if (StringUtils.equals(field.getName(), name)) {
                return field;
            }
        }
        return null;
    }
}
