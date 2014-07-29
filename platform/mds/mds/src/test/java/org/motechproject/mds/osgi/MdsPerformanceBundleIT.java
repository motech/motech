package org.motechproject.mds.osgi;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;

// This test is ignored in order to save time during builds
// remove the exclude directive for it in the top level pom.  It was added there to also prevent the setup from running
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class MdsPerformanceBundleIT extends BasePaxIT {
    private static final Logger logger = LoggerFactory.getLogger(MdsPerformanceBundleIT.class);

    private static final String FOO = "Foo";
    private static final String FOO_CLASS = String.format("%s.%s", Constants.PackagesGenerated.ENTITY, FOO);

    private static final int TEST_INSTANCES = 500;

    private JarGeneratorService generator;
    private EntityService entityService;

    private List<Object> testInstances = new ArrayList<>();

    @Inject
    private BundleContext bundleContext;

    @Before
    public void setUp() throws Exception {
        WebApplicationContext context = ServiceRetriever.getWebAppContext(bundleContext, MDS_BUNDLE_SYMBOLIC_NAME);

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
    public void testPerformance() throws Exception {
        final String serviceName = ClassName.getInterfaceName(FOO_CLASS);

        prepareTestEntity();

        Bundle entitiesBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, MDS_ENTITIES_SYMBOLIC_NAME);
        assertNotNull(entitiesBundle);

        MotechDataService service = (MotechDataService) ServiceRetriever.getService(bundleContext, serviceName);

        Class<?> objectClass = entitiesBundle.loadClass(FOO_CLASS);
        logger.info("Loaded class: " + objectClass.getName());

        compareCreating(service, objectClass);
        compareRetrieval(service);
        compareUpdating(service);
        compareDeleting(service);
    }

    private void compareCreating(MotechDataService service, Class clazz) throws Exception {
        prepareInstances(clazz);

        Long startTime = System.nanoTime();
        for (Object instance : testInstances) {
            service.create(instance);
        }
        Long endTime = (System.nanoTime() - startTime) / 1000000;

        logger.info("MDS Service: Creating " + TEST_INSTANCES + " instances took " + endTime + "ms.");
    }

    private void compareRetrieval(MotechDataService service) {
        Long startTime = System.nanoTime();
        service.retrieveAll();
        Long endTime = (System.nanoTime() - startTime) / 1000000;

        logger.info("MDS Service: Retrieving all instances took " + endTime + "ms.");
    }

    private void compareUpdating(MotechDataService service) {
        List<Object> allObjects = service.retrieveAll();

        Long startTime = System.nanoTime();
        for (Object object : allObjects) {
            service.update(object);
        }
        Long endTime = (System.nanoTime() - startTime) / 1000000;

        logger.info("MDS Service: Updating " + TEST_INSTANCES + " instances took " + endTime + "ms.");
    }

    private void compareDeleting(MotechDataService service) {
        Long startTime = System.nanoTime();
        for (Object object : service.retrieveAll()) {
            service.delete(object);
        }
        Long endTime = (System.nanoTime() - startTime) / 1000000;

        logger.info("MDS Service: Deleting " + TEST_INSTANCES + " instances took " + endTime + "ms.");
    }

    private void prepareInstances(Class<?> clazz) throws Exception {
        Integer someInt = -TEST_INSTANCES / 2;
        String someString = "";
        Random random = new Random(System.currentTimeMillis());


        for (int i = 0; i < TEST_INSTANCES; i++) {
            Object instance = clazz.newInstance();
            MethodUtils.invokeMethod(instance, "setSomeString", someString);
            MethodUtils.invokeMethod(instance, "setSomeInt", someInt);

            testInstances.add(instance);

            someInt++;
            int chars = 1 + random.nextInt(253);
            someString = RandomStringUtils.random(chars);
        }
    }

    private void prepareTestEntity() throws IOException {
        EntityDto entityDto = new EntityDto(9999L, FOO);
        entityDto = entityService.createEntity(entityDto);
        generator.regenerateMdsDataBundle(true);

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
}
