package org.motechproject.mds.osgi;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.QueryParams;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
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
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class MdsBundleIT extends BasePaxIT {
    private static final Logger logger = LoggerFactory.getLogger(MdsBundleIT.class);

    private static final String FOO = "Foo";
    private static final String FOO_CLASS = String.format("%s.%s", Constants.PackagesGenerated.ENTITY, FOO);

    private static final int INSTANCE_COUNT = 5;

    private EntityService entityService;

    @Inject
    private BundleContext bundleContext;

    @Before
    public void setUp() throws Exception {
        WebApplicationContext context = ServiceRetriever.getWebAppContext(bundleContext, MDS_BUNDLE_SYMBOLIC_NAME);

        entityService = context.getBean(EntityService.class);

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

        MotechDataService service = (MotechDataService) ServiceRetriever.getService(bundleContext, serviceName);
        Class<?> objectClass = entitiesBundle.loadClass(FOO_CLASS);
        logger.info("Loaded class: " + objectClass.getName());

        clearInstances(service);

        verifyInstanceCreatingAndRetrieving(service, objectClass);
        verifyInstanceUpdating(service);
        verifyInstanceDeleting(service);
    }

    private void clearInstances(MotechDataService service) {
        for (Object obj : service.retrieveAll()) {
            service.delete(obj);
        }
    }

    private void verifyInstanceCreatingAndRetrieving(MotechDataService service, Class<?> loadedClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        DateTime now = DateUtil.now();

        Object instance = loadedClass.newInstance();
        Object instance2 = loadedClass.newInstance();
        Object instance3 = loadedClass.newInstance();
        Object instance4 = loadedClass.newInstance();
        Object instance5 = loadedClass.newInstance();

        Map<String, TestClass> testMap = new HashMap<>();
        testMap.put("key1", new TestClass(123, "abc"));
        testMap.put("key2", new TestClass(456, "ddd"));

        updateInstance(instance, true, "trueNow", Arrays.asList(1, 2, 3), now, testMap);
        updateInstance(instance2, true, "trueInRange", Arrays.asList("something"), now.plusHours(1), testMap);
        updateInstance(instance3, false, "falseInRange", null, now.plusHours(1), null);
        updateInstance(instance4, true, "trueOutOfRange", null, now.plusHours(10), null);
        updateInstance(instance5, true, "notInSet", null, now, null);

        MethodUtils.invokeMethod(instance, "setSomeMap", testMap);

        service.create(instance);
        Object retrieved = service.retrieveAll().get(0);
        assertInstance(retrieved, true, "trueNow", Arrays.asList(1, 2, 3), now, testMap);

        assertEquals(1, service.retrieveAll().size());
        service.create(instance2);
        service.create(instance3);
        service.create(instance4);
        service.create(instance5);
        assertEquals(INSTANCE_COUNT, service.retrieveAll().size());

        // verify lookups
        Object resultObj = MethodUtils.invokeMethod(service, "byBool",
                new Object[]{ true, QueryParams.ascOrder("someDateTime") });

        assertTrue(resultObj instanceof List);
        List resultList = (List) resultObj;
        assertEquals(4, resultList.size());
        assertInstance(resultList.get(0), true, "trueNow", Arrays.asList(1, 2, 3), now, testMap);

        // only two instances should match this criteria
        resultObj = MethodUtils.invokeMethod(service, "combined",
                new Object[] { true,
                               new Range<>(now.minusHours(1), now.plusHours(5)),
                               new HashSet<>(Arrays.asList("trueNow", "trueInRange", "trueOutOfRange", "falseInRange")),
                               QueryParams.descOrder("someDateTime")});

        assertTrue(resultObj instanceof List);
        resultList = (List) resultObj;
        assertEquals(2, resultList.size());
        assertInstance(resultList.get(0), true, "trueInRange", Arrays.asList("something"), now.plusHours(1), testMap);
        assertInstance(resultList.get(1), true, "trueNow", Arrays.asList(1, 2, 3), now, testMap);
    }

    private void verifyInstanceUpdating(MotechDataService service) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        DateTime dt = DateUtil.now().plusYears(1);

        List<Object> allObjects = service.retrieveAll();
        assertEquals(allObjects.size(), INSTANCE_COUNT);

        Object retrieved = allObjects.get(0);

        Map<String, TestClass> testMap = new HashMap<>();
        testMap.put("key4", new TestClass(4, "ads"));
        testMap.put("key3", new TestClass(21, "test"));

        updateInstance(retrieved, false, "anotherString", Arrays.asList(4, 5), dt, testMap);

        service.update(retrieved);
        Object updated = service.retrieveAll().get(0);

        assertInstance(updated, false, "anotherString", Arrays.asList(4, 5), dt, testMap);
    }

    private void verifyInstanceDeleting(MotechDataService service) throws IllegalAccessException, InstantiationException {
        List<Object> objects = service.retrieveAll();

        for (int i = 0; i < INSTANCE_COUNT; i++) {
            service.delete(objects.get(i));
            assertEquals(INSTANCE_COUNT - i - 1, service.retrieveAll().size());
        }
    }

    private void prepareTestEntities() throws IOException {
        EntityDto entityDto = new EntityDto(9999L, FOO);
        entityDto = entityService.createEntity(entityDto);

        List<FieldDto> fields = new ArrayList<>();
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.BOOLEAN,
                new FieldBasicDto("someBoolean", "someBoolean"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.STRING,
                new FieldBasicDto("someString", "someString"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.LIST,
                new FieldBasicDto("someList", "someList"),
                false, null, null, Arrays.asList(new SettingDto("test", "test", TypeDto.INTEGER, null)), null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.DATETIME,
                new FieldBasicDto("dateTime", "someDateTime"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.MAP,
                new FieldBasicDto("someMap", "someMap"),
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
                                DateTime dateTimeField, Map map)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PropertyUtils.setProperty(instance, "someBoolean", boolField);
        PropertyUtils.setProperty(instance, "someString", stringField);
        PropertyUtils.setProperty(instance, "someList", listField);
        PropertyUtils.setProperty(instance, "someDateTime", dateTimeField);
        PropertyUtils.setProperty(instance, "someMap", map);
    }

    private void assertInstance(Object instance, Boolean boolField, String stringField, List listField,
                                DateTime dateTimeField, Map map)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        assertNotNull(instance);
        assertEquals(boolField, PropertyUtils.getProperty(instance, "someBoolean"));
        assertEquals(stringField, PropertyUtils.getProperty(instance, "someString"));
        assertEquals(listField, PropertyUtils.getProperty(instance, "someList"));
        assertEquals(dateTimeField, PropertyUtils.getProperty(instance, "someDateTime"));
        assertEquals(map, PropertyUtils.getProperty(instance, "someMap"));
    }
}
