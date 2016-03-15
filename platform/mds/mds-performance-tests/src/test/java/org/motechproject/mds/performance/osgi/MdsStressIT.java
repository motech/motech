package org.motechproject.mds.performance.osgi;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.performance.service.MdsDummyDataGenerator;
import org.motechproject.mds.performance.service.impl.MdsDummyDataGeneratorImpl;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MdsStressIT extends LoggingPerformanceIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(MdsStressIT.class);

    private static String FOO;
    private static String FOO_CLASS;

    private static final int TEST_INSTANCES = Integer.parseInt(System.getProperty("mds.performance.quantity"));

    private List<Object> testInstances = new ArrayList<>();

    private MdsDummyDataGenerator generator;
    @Inject
    private BundleContext bundleContext;
    @Inject
    private EntityService entityService;
    @Inject
    private JarGeneratorService jarGeneratorService;

    @Before
    public void setUp() throws Exception {
        setUpSecurityContext("motech_bot", "motech", "mdsSchemaAccess");

        generator = new MdsDummyDataGeneratorImpl(entityService, jarGeneratorService, bundleContext);
        generator.setEntityPrefix("StressITEntity");
        FOO = generator.getEntityPrefix().concat("0");
        FOO_CLASS = String.format("%s.%s", Constants.PackagesGenerated.ENTITY, FOO);
    }

    @Test
    public void testPerformance() throws NotFoundException, CannotCompileException, IOException, InvalidSyntaxException, InterruptedException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final String serviceName = ClassName.getInterfaceName(FOO_CLASS);

        generator.generateDummyEntities(1, 2, 0, true);

        Bundle entitiesBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, MDS_ENTITIES_SYMBOLIC_NAME);
        assertNotNull(entitiesBundle);

        MotechDataService service = (MotechDataService) ServiceRetriever.getService(entitiesBundle.getBundleContext(), serviceName, true);

        Class<?> objectClass = entitiesBundle.loadClass(FOO_CLASS);
        LOGGER.info("Loaded class: " + objectClass.getName());

        stressTestCreating(service, objectClass);
        stressTestRetrieval(service);
        stressTestUpdating(service);
        stressTestDeleting(service);
    }

    private void stressTestCreating(MotechDataService service, Class clazz)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        EntityDto entityDto = entityService.getEntityByClassName(FOO_CLASS);
        for (int i = 0 ; i < TEST_INSTANCES; i++) {
            testInstances.add(generator.makeDummyInstance(entityDto.getId()));
        }

        Long startTime = System.nanoTime();
        for (Object instance : testInstances) {
            service.create(instance);
        }
        Long endTime = (System.nanoTime() - startTime) / 1000000;

        LOGGER.info("MDS Service: Creating " + TEST_INSTANCES + " instances took " + endTime + "ms.");
        logToFile((double) endTime);
    }

    private void stressTestRetrieval(MotechDataService service) {

        Long startTime = System.nanoTime();
        service.retrieveAll();
        Long endTime = (System.nanoTime() - startTime) / 1000000;

        LOGGER.info("MDS Service: Retrieving all instances took " + endTime + "ms.");
        logToFile((double) endTime);
    }

    private void stressTestUpdating(MotechDataService service) {
        List<Object> allObjects = service.retrieveAll();

        Long startTime = System.nanoTime();
        for (Object object : allObjects) {
            service.update(object);
        }
        Long endTime = (System.nanoTime() - startTime) / 1000000;

        LOGGER.info("MDS Service: Updating " + TEST_INSTANCES + " instances took " + endTime + "ms.");
        logToFile((double) endTime);
    }

    private void stressTestDeleting(MotechDataService service) {

        Long startTime = System.nanoTime();
        for (Object object : service.retrieveAll()) {
            service.delete(object);
        }
        Long endTime = (System.nanoTime() - startTime) / 1000000;

        LOGGER.info("MDS Service: Deleting " + TEST_INSTANCES + " instances took " + endTime + "ms.");
        logToFile((double) endTime);
    }

}