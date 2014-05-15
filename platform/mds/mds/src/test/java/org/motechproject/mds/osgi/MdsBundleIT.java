package org.motechproject.mds.osgi;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.testutil.DraftBuilder;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.QueryParams;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
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

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MdsBundleIT extends BasePaxIT {
    private static final Logger logger = LoggerFactory.getLogger(MdsBundleIT.class);

    private static final String FOO = "Foo";
    private static final String FOO_CLASS = String.format("%s.%s", Constants.PackagesGenerated.ENTITY, FOO);

    private static final int INSTANCE_COUNT = 5;

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
    public void testEntitiesBundleInstallsProperly() throws NotFoundException, CannotCompileException, IOException, InvalidSyntaxException, InterruptedException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final String serviceName = ClassName.getInterfaceName(FOO_CLASS);

        prepareTestEntities();

        Bundle entitiesBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, MDS_ENTITIES_SYMBOLIC_NAME);
        assertNotNull(entitiesBundle);

        service = (MotechDataService) ServiceRetriever.getService(bundleContext, serviceName);
        Class<?> objectClass = entitiesBundle.loadClass(FOO_CLASS);
        logger.info("Loaded class: " + objectClass.getName());

        clearInstances();

        verifyInstanceCreatingAndRetrieving(objectClass);
        verifyInstanceUpdating();
        verifyColumnNameChange();
        verifyInstanceDeleting();
    }

    private void clearInstances() {
        for (Object obj : service.retrieveAll()) {
            service.delete(obj);
        }
    }

    private void verifyInstanceCreatingAndRetrieving(Class<?> loadedClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        DateTime now = DateUtil.now();

        Object instance = loadedClass.newInstance();
        Object instance2 = loadedClass.newInstance();
        Object instance3 = loadedClass.newInstance();
        Object instance4 = loadedClass.newInstance();
        Object instance5 = loadedClass.newInstance();

        Map<String, TestClass> testMap = new HashMap<>();
        testMap.put("key1", new TestClass(123, "abc"));
        testMap.put("key2", new TestClass(456, "ddd"));

        Period testPeriod = new Period().withDays(3).withHours(7).withMinutes(50);

        updateInstance(instance, true, "trueNow", Arrays.asList("1", "2", "3"), now, testMap, testPeriod);
        updateInstance(instance2, true, "trueInRange", Arrays.asList("something"), now.plusHours(1), testMap, testPeriod);
        updateInstance(instance3, false, "falseInRange", null, now.plusHours(1), null, testPeriod);
        updateInstance(instance4, true, "trueOutOfRange", null, now.plusHours(10), null, testPeriod);
        updateInstance(instance5, true, "notInSet", null, now, null, testPeriod);

        MethodUtils.invokeMethod(instance, "setSomeMap", testMap);

        service.create(instance);
        Object retrieved = service.retrieveAll().get(0);
        assertInstance(retrieved, true, "trueNow", Arrays.asList("1", "2", "3"), now, testMap, testPeriod);

        assertEquals(1, service.retrieveAll().size());
        service.create(instance2);
        service.create(instance3);
        service.create(instance4);
        service.create(instance5);
        assertEquals(INSTANCE_COUNT, service.retrieveAll().size());

        // verify lookups
        Object resultObj = MethodUtils.invokeMethod(service, "byBool",
                new Object[]{true, QueryParams.ascOrder("someDateTime")});

        assertTrue(resultObj instanceof List);
        List resultList = (List) resultObj;
        assertEquals(4, resultList.size());
        assertInstance(resultList.get(0), true, "trueNow", Arrays.asList("1", "2", "3"), now, testMap, testPeriod);

        // only two instances should match this criteria
        resultObj = MethodUtils.invokeMethod(service, "combined",
                new Object[]{true,
                        new Range<>(now.minusHours(1), now.plusHours(5)),
                        new HashSet<>(Arrays.asList("trueNow", "trueInRange", "trueOutOfRange", "falseInRange")),
                        QueryParams.descOrder("someDateTime")});

        assertTrue(resultObj instanceof List);
        resultList = (List) resultObj;
        assertEquals(2, resultList.size());
        assertInstance(resultList.get(0), true, "trueInRange", Arrays.asList("something"), now.plusHours(1), testMap, testPeriod);
        assertInstance(resultList.get(1), true, "trueNow", Arrays.asList("1", "2", "3"), now, testMap, testPeriod);
    }

    private void verifyInstanceUpdating() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        DateTime dt = DateUtil.now().plusYears(1);

        List<Object> allObjects = service.retrieveAll();
        assertEquals(allObjects.size(), INSTANCE_COUNT);

        Object retrieved = allObjects.get(0);

        Map<String, TestClass> testMap = new HashMap<>();
        testMap.put("key4", new TestClass(4, "ads"));
        testMap.put("key3", new TestClass(21, "test"));

        Period newPeriod = new Period().withYears(2).withMinutes(10);

        updateInstance(retrieved, false, "anotherString", Arrays.asList("4", "5"), dt, testMap, newPeriod);

        service.update(retrieved);
        Object updated = service.retrieveAll().get(0);

        assertInstance(updated, false, "anotherString", Arrays.asList("4", "5"), dt, testMap, newPeriod);
    }

    private void verifyColumnNameChange() throws ClassNotFoundException, InterruptedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Long entityId = entityService.getEntityByClassName(FOO_CLASS).getId();
        List<Field> fieldsDto = entityService.getEntityDraft(entityId).getFields();

        entityService.saveDraftEntityChanges(entityId, DraftBuilder.forFieldEdit(fieldsDto.get(7).getId(), "basic.name", "newFieldName"));
        entityService.commitChanges(entityId);
        generator.regenerateMdsDataBundle(true);

        FieldDto updatedField = entityService.getEntityFields(entityId).get(4);
        assertEquals(updatedField.getBasic().getName(), "newFieldName");

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

    private void verifyInstanceDeleting() throws IllegalAccessException, InstantiationException {
        List<Object> objects = service.retrieveAll();

        for (int i = 0; i < INSTANCE_COUNT; i++) {
            service.delete(objects.get(i));
            assertEquals(INSTANCE_COUNT - i - 1, service.retrieveAll().size());
        }
    }

    private void prepareTestEntities() throws IOException {
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
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                LIST,
                new FieldBasicDto("Some List", "someList"),
                false, null, null,
                Arrays.asList(
                        new SettingDto("mds.form.label.values", new LinkedList<>(), LIST, REQUIRE),
                        new SettingDto("mds.form.label.allowUserSupplied", true, BOOLEAN),
                        new SettingDto("mds.form.label.allowMultipleSelections", true, BOOLEAN)
                ), null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.DATETIME,
                new FieldBasicDto("dateTime", "someDateTime"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.MAP,
                new FieldBasicDto("someMap", "someMap"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.PERIOD,
                new FieldBasicDto("somePeriod", "somePeriod"),
                false, null));


        entityService.addFields(entityDto, fields);

        List<LookupDto> lookups = new ArrayList<>();
        List<LookupFieldDto> lookupFields = new ArrayList<>();

        lookupFields.add(new LookupFieldDto(null, "someBoolean", LookupFieldDto.Type.VALUE));
        lookups.add(new LookupDto("By boolean", false, false, lookupFields, true, "byBool"));

        lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldDto(null, "someBoolean", LookupFieldDto.Type.VALUE));
        lookupFields.add(new LookupFieldDto(null, "someDateTime", LookupFieldDto.Type.RANGE));
        lookupFields.add(new LookupFieldDto(null, "someString", LookupFieldDto.Type.SET));
        lookups.add(new LookupDto("Combined", false, false, lookupFields, true));

        entityService.addLookups(entityDto.getId(), lookups);

        entityService.commitChanges(entityDto.getId());
        generator.regenerateMdsDataBundle(true);
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

    private void clearEntities() {
        for (EntityDto entity : entityService.listEntities()) {
            entityService.deleteEntity(entity.getId());
        }
    }

    private void updateInstance(Object instance, Boolean boolField, String stringField, List listField,
                                DateTime dateTimeField, Map map, Period period)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PropertyUtils.setProperty(instance, "someBoolean", boolField);
        PropertyUtils.setProperty(instance, "someString", stringField);
        PropertyUtils.setProperty(instance, "someList", listField);
        PropertyUtils.setProperty(instance, "someDateTime", dateTimeField);
        PropertyUtils.setProperty(instance, "someMap", map);
        PropertyUtils.setProperty(instance, "somePeriod", period);
    }

    private void assertInstance(Object instance, Boolean boolField, String stringField, List listField,
                                DateTime dateTimeField, Map map, Period period)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        assertNotNull(instance);
        assertEquals(boolField, PropertyUtils.getProperty(instance, "someBoolean"));
        assertEquals(stringField, PropertyUtils.getProperty(instance, "someString"));
        assertEquals(listField, PropertyUtils.getProperty(instance, "someList"));
        assertEquals(dateTimeField, PropertyUtils.getProperty(instance, "someDateTime"));
        assertEquals(map, PropertyUtils.getProperty(instance, "someMap"));
        assertEquals(period, PropertyUtils.getProperty(instance, "somePeriod"));
    }
}
