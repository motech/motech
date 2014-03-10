package org.motechproject.mds.osgi;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.gemini.blueprint.test.platform.OsgiPlatform;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static java.util.Arrays.asList;

public class MdsPerformanceBundleIT extends BaseOsgiIT {
    private static final Logger logger = LoggerFactory.getLogger(MdsPerformanceBundleIT.class);

    private static final String SYSTEM_PACKAGES = "org.osgi.framework.system.packages";
    private static final String MDS_BUNDLE_NAME = "motech-dataservices";
    private static final String MDS_BUNDLE_ENTITIES_NAME = "motech-dataservices-entities";
    private static final String MDS_BUNDLE_SYMBOLIC_NAME = "org.motechproject." + MDS_BUNDLE_NAME;
    private static final String MDS_BUNDLE_ENTITIES_SYMBOLIC_NAME = "org.motechproject." + MDS_BUNDLE_ENTITIES_NAME;

    private static final String FOO = "Foo";
    private static final String FOO_CLASS = String.format("%s.%s", Constants.PackagesGenerated.ENTITY, FOO);

    private static final int TEST_INSTANCES = 500;

    private EntityService entityService;

    private List<Object> testInstances = new ArrayList<>();
    private List<CouchFoo> couchInstances = new ArrayList<>();

    @Override
    public void onSetUp() throws Exception {
        WebApplicationContext context = getContext(MDS_BUNDLE_SYMBOLIC_NAME);
        entityService = (EntityService) context.getBean("entityServiceImpl");

        clearEntities();
        setUpSecurityContext();
    }

    @Override
    public void onTearDown() throws Exception {
        clearEntities();
    }

    // To run performance check, remove "ignored" from the method name
    public void ignoredtestPerformance() throws NotFoundException, CannotCompileException, IOException, InvalidSyntaxException, InterruptedException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final String serviceName = ClassName.getInterfaceName(FOO_CLASS);

        prepareTestEntity();

        Bundle entitiesBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, MDS_BUNDLE_ENTITIES_SYMBOLIC_NAME);
        assertNotNull(entitiesBundle);

        MotechDataService service = (MotechDataService) getService(serviceName);

        Class<?> objectClass = entitiesBundle.loadClass(FOO_CLASS);
        logger.info("Loaded class: " + objectClass.getName());

        StdCouchDbConnector couchDbConnector = (StdCouchDbConnector) getApplicationContext().getBean("testMdsDbConnector");
        CouchMdsRepository couchMdsRepository = new CouchMdsRepository(couchDbConnector);

        compareCreating(service, objectClass, couchMdsRepository);
        compareRetrieval(service, couchMdsRepository);
        compareUpdating(service, couchMdsRepository);
        compareDeleting(service, couchMdsRepository);
    }

    private void compareCreating(MotechDataService service, Class clazz, CouchMdsRepository couchMdsRepository) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        prepareInstances(clazz);

        Long startTime = System.nanoTime();
        for (Object instance : testInstances) {
            service.create(instance);
        }
        Long endTime = (System.nanoTime() - startTime) / 1000000;

        logger.info("MDS Service: Creating " + TEST_INSTANCES + " instances took " + endTime + "ms.");

        startTime = System.nanoTime();
        for (CouchFoo instance : couchInstances) {
            couchMdsRepository.add(instance);
        }
        endTime = (System.nanoTime() - startTime) / 1000000;

        logger.info("CouchDB Repo: Creating " + TEST_INSTANCES + " instances took " + endTime + "ms.");
    }

    private void compareRetrieval(MotechDataService service, CouchMdsRepository couchMdsRepository) {

        Long startTime = System.nanoTime();
        service.retrieveAll();
        Long endTime = (System.nanoTime() - startTime) / 1000000;

        logger.info("MDS Service: Retrieving all instances took " + endTime + "ms.");

        startTime = System.nanoTime();
        couchMdsRepository.getAll();
        endTime = (System.nanoTime() - startTime) / 1000000;

        logger.info("CouchDB repo: Retrieving all instances took " + endTime + "ms.");
    }

    private void compareUpdating(MotechDataService service, CouchMdsRepository couchMdsRepository) {
        List<Object> allObjects = service.retrieveAll();
        List<CouchFoo> allCouchFoos = couchMdsRepository.getAll();

        Long startTime = System.nanoTime();
        for (Object object : allObjects) {
            service.update(object);
        }
        Long endTime = (System.nanoTime() - startTime) / 1000000;

        logger.info("MDS Service: Updating " + TEST_INSTANCES + " instances took " + endTime + "ms.");

        startTime = System.nanoTime();
        for (CouchFoo object : allCouchFoos) {
            couchMdsRepository.update(object);
        }
        endTime = (System.nanoTime() - startTime) / 1000000;

        logger.info("CouchDB repo: Updating " + TEST_INSTANCES + " instances took " + endTime + "ms.");
    }

    private void compareDeleting(MotechDataService service, CouchMdsRepository couchMdsRepository) {

        Long startTime = System.nanoTime();
        for (Object object : service.retrieveAll()) {
            service.delete(object);
        }
        Long endTime = (System.nanoTime() - startTime) / 1000000;

        logger.info("MDS Service: Deleting " + TEST_INSTANCES + " instances took " + endTime + "ms.");

        startTime = System.nanoTime();
        couchMdsRepository.bulkDelete(couchMdsRepository.getAll());
        endTime = (System.nanoTime() - startTime) / 1000000;

        logger.info("CouchDB repo: Deleting " + TEST_INSTANCES + " instances took " + endTime + "ms.");
    }

    private void prepareInstances(Class<?> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Integer someInt = -TEST_INSTANCES/2;
        String someString = "";
        Random random = new Random(System.currentTimeMillis());


        for (int i=0; i<TEST_INSTANCES; i++) {
            Object instance = clazz.newInstance();
            MethodUtils.invokeMethod(instance, "setSomeString", someString);
            MethodUtils.invokeMethod(instance, "setSomeInt", someInt);

            testInstances.add(instance);
            couchInstances.add(new CouchFoo(someInt, someString));

            someInt++;
            int chars = 1 + random.nextInt(253);
            someString = RandomStringUtils.random(chars);
        }
    }

    private void prepareTestEntity() throws IOException {
        EntityDto entityDto = new EntityDto(9999L, FOO);
        entityDto = entityService.createEntity(entityDto);

        List<FieldDto> fields = new ArrayList<>();
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.INTEGER,
                new FieldBasicDto("someInt", "someInt"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.STRING,
                new FieldBasicDto("someString", "someString"),
                false, null));

        entityService.addFields(entityDto, fields);
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

    @Override
    protected String[] getTestBundlesNames() {
        // Paranamer-sources is not parsed properly by the base class, so we remove it from our dependencies
        // Apache Felix Framework seem to be duplicated somewhere what causes exception, so we remove additional one
        String[] names = super.getTestBundlesNames();
        String[] toRemove = { "com.thoughtworks.paranamer,paranamer,sources",
                "org.apache.felix,org.apache.felix.framework,3.2.0" };

        return removeTestBundles(names, toRemove);
    }

    @Override
    protected OsgiPlatform createPlatform() {
        OsgiPlatform platform = super.createPlatform();

        try (InputStream in = getClass().getResourceAsStream("/osgi.properties")) {
            Properties osgiProperties = new Properties();
            osgiProperties.load(in);

            platform.getConfigurationProperties().setProperty(SYSTEM_PACKAGES, osgiProperties.getProperty(SYSTEM_PACKAGES));
        } catch (IOException e) {
            logger.error("Cannot read osgi.properties", e);
        }

        return platform;
    }

    private String[] removeTestBundles(String[] initialArray, String[] toRemove) {
        for (String bundle : toRemove) {
            initialArray = (String[]) ArrayUtils.removeElement(initialArray, bundle);
        }
        return initialArray;
    }

    private void clearEntities() {
        for (EntityDto entity : entityService.listEntities()) {
            entityService.deleteEntity(entity.getId());
        }

    }

    private WebApplicationContext getContext(String bundleName) throws InvalidSyntaxException, InterruptedException {
        WebApplicationContext theContext = null;

        int tries = 0;

        do {
            ServiceReference[] references =
                    bundleContext.getAllServiceReferences(WebApplicationContext.class.getName(), null);

            for (ServiceReference ref : references) {
                if (bundleName.equals(ref.getBundle().getSymbolicName())) {
                    theContext = (WebApplicationContext) bundleContext.getService(ref);
                    break;
                }
            }

            ++tries;
            Thread.sleep(2000);
        } while (theContext == null && tries < 5);

        assertNotNull("Unable to retrieve the bundle context", theContext);

        return theContext;
    }

    private Object getService(String className) throws InterruptedException {
        Object service = null;

        int tries = 0;

        do {
            ServiceReference ref = bundleContext.getServiceReference(className);

            if (ref != null) {
                service = bundleContext.getService(ref);
                break;
            }

            ++tries;
            Thread.sleep(5000);
        } while (tries < 5);

        assertNotNull("Unable to retrieve the service " + className, service);

        return service;
    }

    @Override
    protected List<String> getImports() {
        return asList(
                "org.codehaus.jackson",
                "org.motechproject.commons.couchdb.model",
                "org.motechproject.commons.couchdb.service",
                "org.motechproject.mds.domain",
                "org.motechproject.mds.repository",
                "org.motechproject.mds.service",
                "org.motechproject.mds.service.impl",
                "org.motechproject.mds.util"
        );
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{ "testMdsAndCouchContext.xml" };
    }

    @View(name = "all", map = "function(doc) { emit(doc._id, doc); }")
    private class CouchMdsRepository extends MotechBaseRepository<CouchFoo> {

        public CouchMdsRepository(CouchDbConnector db) {
            super(CouchFoo.class, db);
        }
    }

}
