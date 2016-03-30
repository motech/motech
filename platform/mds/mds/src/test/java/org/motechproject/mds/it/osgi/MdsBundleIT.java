package org.motechproject.mds.it.osgi;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.datanucleus.PropertyNames;
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
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.mds.domain.EntityDraft;
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
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.UserPreferencesDto;
import org.motechproject.mds.osgi.TestClass;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryExecutor;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.query.SqlQueryExecution;
import org.motechproject.mds.service.ComboboxValueService;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MDSLookupService;
import org.motechproject.mds.service.MetadataService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.RestDocumentationService;
import org.motechproject.mds.service.UserPreferencesService;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
import java.util.Properties;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    private static final String CLASS_NAME = "org.motechproject.sampleModule.SampleEntity";
    private static final String USERNAME = "motech";
    private static final int INSTANCE_COUNT = 5;
    private static final Byte[] BYTE_ARRAY_VALUE = new Byte[]{110, 111, 112};
    private static final Period TEST_PERIOD = new Period().withDays(3).withHours(7).withMinutes(50);
    private static final Period NEW_PERIOD = new Period().withYears(2).withMinutes(10);
    private static final Map<String, TestClass> TEST_MAP = new HashMap<>();
    private static final Map<String, TestClass> TEST_MAP2 = new HashMap<>();
    private static final DateTime NOW = DateUtil.now().secondOfMinute().roundFloorCopy();
    private static final LocalDateTime JAVA_NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    private static final DateTime YEAR_LATER = NOW.plusYears(1);
    private static final LocalDate LD_NOW = NOW.toLocalDate();
    private static final java.time.LocalDate JAVA_LD_NOW = JAVA_NOW.toLocalDate();
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
    private File configurationFile;
    private String startingCacheType;

    @Inject
    private SqlDBManager sqlDBManager;

    @Inject
    private BundleContext bundleContext;

    @Inject
    private MDSLookupService mdsLookupService;

    @Inject
    private RestDocumentationService restDocService;

    @Inject
    private CoreConfigurationService coreConfigurationService;

    @Inject
    private UserPreferencesService userPreferencesService;

    @Before
    public void setUp() throws Exception {
        WebApplicationContext context = ServiceRetriever.getWebAppContext(bundleContext, MDS_BUNDLE_SYMBOLIC_NAME, 10000, 12);

        entityService = context.getBean(EntityService.class);
        generator = context.getBean(JarGeneratorService.class);

        clearEntities();
        setUpSecurityContextForDefaultUser("mdsSchemaAccess");

        //Prepare file with datanucleus configuration for later use to
        //turn off and on l2 cache
        configurationFile = new File(coreConfigurationService.getConfigLocation().getLocation()
                .concat("/" + ConfigurationConstants.DATANUCLEUS_DATA_SETTINGS_FILE_NAME));
        //Remember starting cache type. It will be restored after tests
        startingCacheType = coreConfigurationService.loadDatanucleusDataConfig().getProperty("datanucleus.cache.level2.type");
    }

    @After
    public void tearDown() throws Exception {
        clearEntities();
        setCacheType(startingCacheType);
    }

    @Test
    public void testEntitiesBundleInstallsProperly() throws Exception {

        //Run tests with l2 cache active
        testEntitiesBundleInstallsProperly(true);

        //Run tests without l2 cache
        setCacheType("none");
        testEntitiesBundleInstallsProperly(false);
    }

    private void testEntitiesBundleInstallsProperly(boolean withCache) throws Exception {
        final String serviceName = ClassName.getInterfaceName(FOO_CLASS);

        //Some additional preparation needed for second run without l2 cache
        if(!withCache) {
            clearEntities();
            SchemaHolder schemaHolder = entityService.getSchema();
            generator.regenerateMdsDataBundle(schemaHolder);
        }

        prepareTestEntities();

        Bundle entitiesBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, MDS_ENTITIES_SYMBOLIC_NAME);
        assertNotNull(entitiesBundle);

        service = (MotechDataService) ServiceRetriever.getService(bundleContext, serviceName, true);

        Class<?> objectClass = entitiesBundle.loadClass(FOO_CLASS);
        getLogger().info("Loaded class: " + objectClass.getName());

        clearInstances();

        verifyMetadataRetrieval();
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
        verifyUniqueConstraint();

        service.deleteAll();
    }

    @Test
    public void testUserPreferences() {
        EntityDto entityDto = createEntityForPreferencesTest();

        // first retrieve - should create default user preferences for entity
        UserPreferencesDto userPreferencesDto = userPreferencesService.getUserPreferences(entityDto.getId(), USERNAME);

        assertEquals(new Integer(50), userPreferencesDto.getGridRowsNumber());
        assertEquals(3, userPreferencesDto.getVisibleFields().size());
        assertTrue(userPreferencesDto.getVisibleFields().contains("someBoolean"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("someString"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("someInteger"));

        assertEquals(0, userPreferencesDto.getSelectedFields().size());
        assertEquals(0, userPreferencesDto.getUnselectedFields().size());

        userPreferencesService.updateGridSize(entityDto.getId(), USERNAME, 100);
        userPreferencesDto = userPreferencesService.getUserPreferences(entityDto.getId(), USERNAME);
        assertEquals(new Integer(100), userPreferencesDto.getGridRowsNumber());

        // if null then default value from settings will be used
        userPreferencesService.updateGridSize(entityDto.getId(), USERNAME, null);
        userPreferencesDto = userPreferencesService.getUserPreferences(entityDto.getId(), USERNAME);
        assertEquals(new Integer(50), userPreferencesDto.getGridRowsNumber());

        userPreferencesService.unselectField(entityDto.getId(), USERNAME, "someString");
        userPreferencesDto = userPreferencesService.getUserPreferences(entityDto.getId(), USERNAME);

        assertEquals(2, userPreferencesDto.getVisibleFields().size());
        assertTrue(userPreferencesDto.getVisibleFields().contains("someBoolean"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("someInteger"));

        assertEquals(0, userPreferencesDto.getSelectedFields().size());
        assertEquals(1, userPreferencesDto.getUnselectedFields().size());
        assertTrue(userPreferencesDto.getUnselectedFields().contains("someString"));

        userPreferencesService.selectField(entityDto.getId(), USERNAME, "otherInteger");
        userPreferencesDto = userPreferencesService.getUserPreferences(entityDto.getId(), USERNAME);

        assertEquals(3, userPreferencesDto.getVisibleFields().size());
        assertTrue(userPreferencesDto.getVisibleFields().contains("someBoolean"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("someInteger"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("otherInteger"));

        assertEquals(1, userPreferencesDto.getSelectedFields().size());
        assertTrue(userPreferencesDto.getSelectedFields().contains("otherInteger"));
        assertEquals(1, userPreferencesDto.getUnselectedFields().size());
        assertTrue(userPreferencesDto.getUnselectedFields().contains("someString"));

        userPreferencesService.selectField(entityDto.getId(), USERNAME, "someString");
        userPreferencesDto = userPreferencesService.getUserPreferences(entityDto.getId(), USERNAME);

        assertEquals(4, userPreferencesDto.getVisibleFields().size());
        assertTrue(userPreferencesDto.getVisibleFields().contains("someBoolean"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("otherInteger"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("otherInteger"));
        assertTrue(userPreferencesDto.getVisibleFields().contains("someString"));

        assertEquals(2, userPreferencesDto.getSelectedFields().size());
        assertTrue(userPreferencesDto.getSelectedFields().contains("otherInteger"));
        assertTrue(userPreferencesDto.getSelectedFields().contains("someString"));
        assertEquals(0, userPreferencesDto.getUnselectedFields().size());

        userPreferencesService.unselectFields(entityDto.getId(), USERNAME);
        userPreferencesDto = userPreferencesService.getUserPreferences(entityDto.getId(), USERNAME);
        assertEquals(0, userPreferencesDto.getVisibleFields().size());
        assertEquals(0, userPreferencesDto.getSelectedFields().size());
        assertEquals(10, userPreferencesDto.getUnselectedFields().size());

        userPreferencesService.selectFields(entityDto.getId(), USERNAME);
        userPreferencesDto = userPreferencesService.getUserPreferences(entityDto.getId(), USERNAME);
        assertEquals(10, userPreferencesDto.getVisibleFields().size());
        assertEquals(10, userPreferencesDto.getSelectedFields().size());
        assertEquals(0, userPreferencesDto.getUnselectedFields().size());

        // if field will be removed from entity then it should be removed also from preferences (CASCADE)
        EntityDraft draft = entityService.getEntityDraft(entityDto.getId());
        List<FieldDto> fields1 = entityService.getEntityFields(draft.getId());
        Long someIntegerId = getFieldIdByName(fields1, "someInteger");

        DraftData draftData = DraftBuilder.forFieldRemoval(someIntegerId);
        entityService.saveDraftEntityChanges(entityDto.getId(), draftData);
        entityService.commitChanges(entityDto.getId());

        userPreferencesDto = userPreferencesService.getUserPreferences(entityDto.getId(), USERNAME);
        assertEquals(9, userPreferencesDto.getVisibleFields().size());
        assertTrue(userPreferencesDto.getSelectedFields().contains("someBoolean"));
        assertTrue(userPreferencesDto.getSelectedFields().contains("someString"));
        assertFalse(userPreferencesDto.getSelectedFields().contains("someInteger"));
    }

    private EntityDto createEntityForPreferencesTest() {
        EntityDto entityDto = new EntityDto(null, CLASS_NAME);
        entityDto = entityService.createEntity(entityDto);

        List<FieldDto> fields = new ArrayList<>();
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.BOOLEAN,
                new FieldBasicDto("Some Boolean", "someBoolean"),
                false, false, false, false, false, null, null, null, null));

        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.STRING,
                new FieldBasicDto("Some String", "someString"),
                false, false, false, false, false, null, null,
                asList(
                        new SettingDto("mds.form.label.textarea", false, BOOLEAN)
                ), null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.INTEGER,
                new FieldBasicDto("Some Integer", "someInteger"),
                false, false, false, false, false, null, null, null, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.INTEGER,
                new FieldBasicDto("Other Integer", "otherInteger"),
                false, false, false, false, false, null, null, null, null));

        entityService.addFields(entityDto, fields);

        Map<String, Long> displayedFields = new HashMap<>();
        displayedFields.put("someInteger", 1L);
        displayedFields.put("someBoolean", 2L);
        displayedFields.put("someString", 3L);

        entityService.addDisplayedFields(entityDto, displayedFields);

        return entityDto;
    }

    private void verifyComboboxDataMigration() throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Long entityId = entityService.getEntityByClassName(FOO_CLASS).getId();
        Long fieldId = getFieldIdByName(entityService.getFields(entityId), "someEnum");

        DraftData draft = DraftBuilder.forFieldEdit(fieldId, "settings.2.value", true);

        entityService.saveDraftEntityChanges(entityId, draft);
        entityService.commitChanges(entityId);

        SchemaHolder schemaHolder = entityService.getSchema();

        generator.regenerateMdsDataBundle(schemaHolder, true);
        service = (MotechDataService) ServiceRetriever.getService(bundleContext, ClassName.getInterfaceName(FOO_CLASS), true);

        assertValuesEqual(getExpectedComboboxValues(), getValues(service.detachedCopyAll(service.retrieveAll())));
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
        for (FieldDto field : fields) {
            if(field.getBasic().getName().equals(name)) {
                return field.getId();
            }
        }

        return null;
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

        // instance 1
        updateInstance(instance, true, "trueNow", "trueNowCp", new ArrayList(asList("1", "2", "3")),
                       NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1, toEnum(loadedClass, "one"),
                       JAVA_LD_NOW, JAVA_NOW);

        // instance 2
        updateInstance(instance2, true, "trueInRange", "trueInRangeCp", new ArrayList(asList("2", "4")),
                       NOW.plusHours(1), LD_NOW.plusDays(1), TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 2, toEnum(loadedClass, "two"),
                       JAVA_LD_NOW.plusDays(1), JAVA_NOW.plusHours(1));

        // instance 3
        updateInstance(instance3, false, "falseInRange", "falseInRangeCp", null,
                       NOW.plusHours(2), LD_NOW.plusDays(1), null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 2, toEnum(loadedClass, "three"),
                       JAVA_LD_NOW.plusDays(2), JAVA_NOW.plusHours(2));

        // instance 4
        updateInstance(instance4, true, "trueOutOfRange", "trueOutOfRangeCp", null,
                       NOW.plusHours(3), LD_NOW.plusDays(10), null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 3, toEnum(loadedClass, "one"),
                       JAVA_LD_NOW.plusDays(3), JAVA_NOW.plusHours(3));

        // instance 5
        updateInstance(instance5, true, "notInSet", "notInSetCp", null,
                       NOW.plusHours(4), LD_NOW, null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                       DATE_NOW, DOUBLE_VALUE_2, MORNING_TIME, 4, toEnum(loadedClass, "two"),
                       JAVA_LD_NOW.plusDays(4), JAVA_NOW.plusHours(4));

        //Single object return lookup should return 0 if there are no instances
        Long emptyCount = (Long) MethodUtils.invokeMethod(service, "countByUniqueString", "trueNow");
        assertEquals(emptyCount, (Long) 0L);

        service.create(instance);
        Object retrieved = service.retrieveAll(QueryParams.ascOrder("someDateTime")).get(0);

        //Single object return lookup should return 1 if there is instance with unique value in field
        Long count = (Long) MethodUtils.invokeMethod(service, "countByUniqueString", "trueNow");
        assertEquals(count, (Long) 1L);

        assertInstanceOne(retrieved, loadedClass);

        assertEquals(1, service.retrieveAll().size());
        service.create(instance2);
        service.create(instance3);
        service.create(instance4);
        service.create(instance5);
        assertEquals(INSTANCE_COUNT, service.retrieveAll().size());

        // verify double order
        QueryParams queryParams = new QueryParams(asList(new Order("someLocalDate", Order.Direction.DESC),
                new Order("someString", Order.Direction.ASC)));
        List<Object> result = service.retrieveAll(queryParams);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertInstanceFour(result.get(0), loadedClass);
        assertInstanceThree(result.get(1), loadedClass);
        assertInstanceTwo(result.get(2), loadedClass);
        assertInstanceFive(result.get(3), loadedClass);
        assertInstanceOne(result.get(4), loadedClass);
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

        assertInstanceOne(resultList.get(0), objClass);

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
        assertInstanceOne(resultList.get(0), objClass);

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
        assertInstanceTwo(resultList.get(0), objClass);
        assertInstanceOne(resultList.get(1), objClass);

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
        assertInstanceOne(resultList.get(0), objClass);
        assertInstanceTwo(resultList.get(1), objClass);
        assertInstanceThree(resultList.get(2), objClass);

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
            assertInstanceOne(resultList.get(0), objClass);
            assertInstanceTwo(resultList.get(1), objClass);
            assertInstanceFour(resultList.get(2), objClass);
        }
    }

    private void verifyInstanceUpdating() throws Exception {
        getLogger().info("Verifying instance updating");

        List<Object> allObjects = service.retrieveAll(QueryParams.descOrder("someDateTime"));
        assertEquals(allObjects.size(), INSTANCE_COUNT);

        Object retrieved = allObjects.get(0);
        Class objClass = retrieved.getClass();

        // instance 1.1
        updateInstance(retrieved, false, "anotherString", "anotherStringCp", new ArrayList(asList("4", "5")),
                YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 10, toEnum(objClass, "two"),
                JAVA_LD_NOW.plusDays(5), JAVA_NOW.plusHours(5));

        service.update(retrieved);
        Object updated = service.retrieveAll(QueryParams.descOrder("someDateTime")).get(0);

        assertInstanceOneDotOne(updated, objClass);
    }

    private void verifyInstanceCreatingOrUpdating(Class<?> loadedClass) throws Exception {
        getLogger().info("Verifying instance creating or updating using createOrUpdate() method");

        // Creating a new object using createOrUpdate() method and checking if it was really added
        Object instance = loadedClass.newInstance();

        updateInstance(instance, false, "newInstance", "newInstance", new ArrayList(asList("1", "2", "3")),
                NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1, toEnum(loadedClass, "one"),
                JAVA_LD_NOW.plusDays(6), JAVA_NOW.plusHours(6));

        service.createOrUpdate(instance);                           // using createOrUpdate() to create

        List<Object> allObjects = service.retrieveAll();
        assertEquals(allObjects.size(), INSTANCE_COUNT + 1);        // should return one extra object


        // Now update that object using createOrUpdate method and check if it is really updated
        Object retrieved = allObjects.get(INSTANCE_COUNT);          // gets the last added object
        Class objClass = retrieved.getClass();

        updateInstance(retrieved, false, "yetAnotherString", "yetAnotherStringCp", new ArrayList(asList("1", "2", "3")),
                YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 10, toEnum(objClass, "two"),
                JAVA_LD_NOW.plusDays(7), JAVA_NOW.plusHours(7));

        service.createOrUpdate(retrieved);                          // using createOrUpdate() to update

        assertEquals(allObjects.size(), INSTANCE_COUNT + 1);        // number of objects shouldn't change since last check

        Object updated = service.retrieveAll().get(INSTANCE_COUNT); // gets the last added object

        assertInstance(updated, false, "yetAnotherString", "yetAnotherStringCp", asList("1", "2", "3"),
                YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 10, toEnum(objClass, "two"),
                JAVA_LD_NOW.plusDays(7), JAVA_NOW.plusHours(7));

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

        SchemaHolder schemaHolder = entityService.getSchema();
        generator.regenerateMdsDataBundle(schemaHolder);

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

        schemaHolder = entityService.getSchema();
        generator.regenerateMdsDataBundle(schemaHolder);

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
        assertInstanceOneDotOne(result.get(0), objClass);

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
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 3, toEnum(objClass, "two"),
                JAVA_LD_NOW.minusDays(1), JAVA_NOW.minusHours(1));
        service.update(retrieved);

        ComboboxValueService cbValueService = ServiceRetriever.getService(bundleContext, ComboboxValueService.class);

        assertEquals(asList("0", "2", "35", "4"), cbValueService.getAllValuesForCombobox(FOO_CLASS, "someList"));
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
        assertInstance(list.get(0), false, "fromCsv2", "Capital CSV", Collections.emptyList(), null, new LocalDate(2012, 10, 14),
                null, new Period(2, 0, 0, 0, 0, 0, 0, 0), null, new DateTime(2014, 12, 2, 16, 13, 40, 0, DateTimeZone.UTC).toDate(),
                null, new Time(20, 20), null, null, null, null);
        assertInstance(list.get(1), true, "fromCsv1", "Capital CSV", new ArrayList(asList("one", "two")),
                new DateTime(2014, 12, 2, 13, 10, 40, 0, DateTimeZone.UTC).withZone(DateTimeZone.getDefault()),
                new LocalDate(2012, 10, 15), null, new Period(1, 0, 0, 0, 0, 0, 0, 0), null,
                new DateTime(2014, 12, 2, 13, 13, 40, 0, DateTimeZone.UTC).toDate(), null, new Time(10, 30),
                null, null, null, null);
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

    private void verifyMetadataRetrieval() {
        MetadataService metadataService = ServiceRetriever.getService(bundleContext, MetadataService.class);
        assertEquals("MDS_FOO_SOMELIST", metadataService.getComboboxTableName(FOO_CLASS, "someList"));
    }

    private void verifyUniqueConstraint() throws Exception {
        Object instance = service.getClassType().newInstance();
        PropertyUtils.setProperty(instance, "someString", "uniqueVal");

        service.create(instance);

        Object dupeInstance = service.getClassType().newInstance();
        PropertyUtils.setProperty(dupeInstance, "someString", "uniqueVal");

        // this should violate the unique constraint

        boolean caught = false;
        try {
            service.create(dupeInstance);
        } catch (RuntimeException e) {
            caught = true;
        }

        assertTrue("Unique constraint had no effect!", caught);

        // verify constraint removal
        Long entityId = entityService.getEntityByClassName(FOO_CLASS).getId();
        Long fieldId = getFieldIdByName(entityService.getFields(entityId), "someString");

        DraftData draft = DraftBuilder.forFieldEdit(fieldId, "basic.unique", false);

        entityService.saveDraftEntityChanges(entityId, draft);
        entityService.commitChanges(entityId);

        SchemaHolder schemaHolder = entityService.getSchema();
        generator.regenerateMdsDataBundle(schemaHolder, true);
        service = (MotechDataService) ServiceRetriever.getService(bundleContext, ClassName.getInterfaceName(FOO_CLASS), true);

        // should succeed now, no exception is enough for us at this point
        dupeInstance = service.getClassType().newInstance();
        PropertyUtils.setProperty(dupeInstance, "someString", "uniqueVal");

        service.create(dupeInstance);
    }

    private void prepareTestEntities() throws IOException {
        getLogger().info("Preparing entities for testing");

        EntityDto entityDto = new EntityDto(9999L, FOO);
        entityDto = entityService.createEntity(entityDto);

        SchemaHolder schemaHolder = entityService.getSchema();
        generator.regenerateMdsDataBundle(schemaHolder);

        List<FieldDto> fields = new ArrayList<>();
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.BOOLEAN,
                new FieldBasicDto("Some Boolean", "someBoolean"),
                false, null));
        // this field is unique
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.STRING,
                new FieldBasicDto("Some String", "someString", false, true),
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
                TypeDto.DATETIME8,
                new FieldBasicDto("someJavaDateTime", "someJavaDateTime"),
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
                TypeDto.LOCAL_DATE8,
                new FieldBasicDto("someJavaDate", "someJavaDate"),
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

        schemaHolder = entityService.getSchema();
        generator.regenerateMdsDataBundle(schemaHolder);

        getLogger().info("Entities ready for testing");
    }

    private void clearEntities() {
        getLogger().info("Cleaning up entities");

        for (EntityDto entity : entityService.listEntities()) {
            if (!entity.isDDE()) {
                userPreferencesService.removeUserPreferences(entity.getId(), USERNAME);
                entityService.deleteEntity(entity.getId());
            }
        }
    }

    private void updateInstance(Object instance, Boolean boolField, String stringField, String capitalizedStrField, List listField,
                                DateTime dateTimeField, LocalDate localDateField, Map map, Period period,
                                Byte[] blob, Date dateField, Double decimalField, Time timeField, Integer intField,
                                Object enumVal, java.time.LocalDate javaDateField, LocalDateTime javaDateTimeField)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PropertyUtils.setProperty(instance, "someBoolean", boolField);
        PropertyUtils.setProperty(instance, "someString", stringField);
        PropertyUtil.safeSetProperty(instance, "CapitalName", capitalizedStrField);
        PropertyUtils.setProperty(instance, "someList", listField);
        PropertyUtils.setProperty(instance, "someDateTime", dateTimeField);
        PropertyUtils.setProperty(instance, "someJavaDateTime", javaDateTimeField);
        PropertyUtils.setProperty(instance, "someLocalDate", localDateField);
        PropertyUtils.setProperty(instance, "someJavaDate", javaDateField);
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
                                Object enumVal, java.time.LocalDate javaDateField, LocalDateTime javaDateTimeField)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        assertNotNull(instance);
        assertEquals(boolField, PropertyUtils.getProperty(instance, "someBoolean"));
        assertEquals(stringField, PropertyUtils.getProperty(instance, "someString"));
        assertEquals(capitalizedStrField, PropertyUtil.safeGetProperty(instance, "CapitalName"));
        assertEquals(dateTimeField, PropertyUtils.getProperty(instance, "someDateTime"));
        assertEquals(map, PropertyUtils.getProperty(instance, "someMap"));
        assertEquals(period, PropertyUtils.getProperty(instance, "somePeriod"));
        assertEquals(localDateField, PropertyUtils.getProperty(instance, "someLocalDate"));
        assertEquals(dateField, PropertyUtils.getProperty(instance, "someDate"));
        assertEquals(decimalField, PropertyUtils.getProperty(instance, "someDecimal"));
        assertEquals(timeField, PropertyUtils.getProperty(instance, "someTime"));
        assertEquals(intField, PropertyUtils.getProperty(instance, "someInt"));
        assertEquals(enumVal, PropertyUtils.getProperty(instance, "someEnum"));
        assertEquals(javaDateField, PropertyUtils.getProperty(instance, "someJavaDate"));
        assertEquals(javaDateTimeField, PropertyUtils.getProperty(instance, "someJavaDateTime"));

        // assert blob
        Object blobValue = service.getDetachedField(instance, "someBlob");
        assertEquals(Arrays.toString(blob), Arrays.toString((Byte[]) blobValue));

        Object comboboxValue = service.getDetachedField(instance, "someList");
        assertEquals(listField, comboboxValue);
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

    private void setCacheType(String type) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(configurationFile)) {
            Properties datanucleusProps = coreConfigurationService.loadDatanucleusDataConfig();

            datanucleusProps.remove(PropertyNames.PROPERTY_VALIDATION_FACTORY);

            if (type != null) {
                datanucleusProps.setProperty("datanucleus.cache.level2.type", type);
            } else {
                datanucleusProps.remove("datanucleus.cache.level2.type");
            }
            datanucleusProps.store(outputStream, null);
        }
    }

    private void assertInstanceOne(Object instance, Class objClass) throws Exception {
        assertInstance(instance, true, "trueNow", "trueNowCp", asList("1", "2", "3"),
                NOW, LD_NOW, TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 1, toEnum(objClass, "one"),
                JAVA_LD_NOW, JAVA_NOW);
    }

    private void assertInstanceTwo(Object instance, Class objClass) throws Exception {
        assertInstance(instance, true, "trueInRange", "trueInRangeCp", asList("2", "4"),
                NOW.plusHours(1), LD_NOW.plusDays(1), TEST_MAP, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_1, MORNING_TIME, 2, toEnum(objClass, "two"),
                JAVA_LD_NOW.plusDays(1), JAVA_NOW.plusHours(1));
    }

    private void assertInstanceThree(Object instance, Class objClass) throws Exception {
        assertInstance(instance, false, "falseInRange", "falseInRangeCp", Collections.emptyList(),
                NOW.plusHours(2), LD_NOW.plusDays(1), null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 2, toEnum(objClass, "three"),
                JAVA_LD_NOW.plusDays(2), JAVA_NOW.plusHours(2));
    }

    private void assertInstanceFour(Object instance, Class objClass) throws Exception {
        assertInstance(instance, true, "trueOutOfRange", "trueOutOfRangeCp", Collections.emptyList(),
                NOW.plusHours(3), LD_NOW.plusDays(10), null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 3, toEnum(objClass, "one"),
                JAVA_LD_NOW.plusDays(3), JAVA_NOW.plusHours(3));
    }

    private void assertInstanceFive(Object instance, Class objClass) throws Exception {
        assertInstance(instance, true, "notInSet", "notInSetCp", Collections.emptyList(),
                NOW.plusHours(4), LD_NOW, null, TEST_PERIOD, BYTE_ARRAY_VALUE,
                DATE_NOW, DOUBLE_VALUE_2, MORNING_TIME, 4, toEnum(objClass, "two"),
                JAVA_LD_NOW.plusDays(4), JAVA_NOW.plusHours(4));
    }

    private void assertInstanceOneDotOne(Object instance, Class objClass) throws Exception {
        assertInstance(instance, false, "anotherString", "anotherStringCp", asList("4", "5"),
                YEAR_LATER, LD_YEAR_AGO, TEST_MAP2, NEW_PERIOD, BYTE_ARRAY_VALUE,
                DATE_TOMORROW, DOUBLE_VALUE_2, NIGHT_TIME, 10, toEnum(objClass, "two"),
                JAVA_LD_NOW.plusDays(5), JAVA_NOW.plusHours(5));
    }
}
