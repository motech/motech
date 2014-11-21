package org.motechproject.mds.performance.osgi;

import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.performance.domain.Sample;
import org.motechproject.mds.performance.service.MdsDummyDataGenerator;
import org.motechproject.mds.performance.service.SampleService;
import org.motechproject.mds.performance.service.impl.MdsDummyDataGeneratorImpl;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MdsDummyDataGeneratorIT extends BasePaxIT {

    private final static int ENTITIES = 5;
    private final static int FIELDS = 5;
    private final static int LOOKUPS = 5;

    private final static int AUTO_GENERATED_FIELDS = 6;

    private final static int INSTANCES = Integer.parseInt(System.getProperty("mds.performance.quantity"));

    private MdsDummyDataGenerator generator;
    @Inject
    private EntityService entityService;
    @Inject
    private JarGeneratorService jarGeneratorService;
    @Inject
    private BundleContext bundleContext;

    @Before
    public void setUp() {
        generator = new MdsDummyDataGeneratorImpl(entityService, jarGeneratorService, bundleContext);
        generator.clearEntities();
        generator.setUpSecurityContext();
        generator.setEntityPrefix("DummyGeneratorITEntity");
    }

    @Test
    public void verifyDummyDataGeneratorIsFunctional()
            throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        verifyEudeGetsCreated();
        verifyEUDEInstancesAreCreated();
        verifyDDEInstancesAreCreated();
    }

    private void verifyEudeGetsCreated() throws IOException {
        generator.generateDummyEntities(ENTITIES, FIELDS, LOOKUPS, true);
        EntityDto entityDto = entityService.getEntityByClassName
                (Constants.Packages.ENTITY.concat(".").concat(generator.getEntityPrefix()).concat("0"));

        EntityDto entityDto2 = entityService.getEntityByClassName
                (Constants.Packages.ENTITY.concat(".").concat(generator.getEntityPrefix()).concat(String.valueOf(ENTITIES - 1)));

        // Verify the entities were created
        Assert.assertNotNull(entityDto);
        Assert.assertNotNull(entityDto2);

        // Verify the correct amount of fields was added
        Assert.assertEquals(entityService.getFields(entityDto.getId()).size(), FIELDS + AUTO_GENERATED_FIELDS);
        Assert.assertEquals(entityService.getFields(entityDto2.getId()).size(), FIELDS + AUTO_GENERATED_FIELDS);

        // Verify the correct amount of lookups was added
        Assert.assertEquals(entityService.getEntityLookups(entityDto.getId()).size(), LOOKUPS);
        Assert.assertEquals(entityService.getEntityLookups(entityDto2.getId()).size(), LOOKUPS);
    }

    private void verifyEUDEInstancesAreCreated()
            throws IllegalAccessException, ClassNotFoundException, InstantiationException {

        EntityDto entityDto = entityService.getEntityByClassName
                (Constants.Packages.ENTITY.concat(".").concat(generator.getEntityPrefix()).concat("0"));
        Assert.assertNotNull(entityDto);

        generator.generateDummyInstances(entityDto.getId(), INSTANCES);

        Bundle entitiesBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, MDS_ENTITIES_SYMBOLIC_NAME);
        assertNotNull(entitiesBundle);
        final String serviceName = ClassName.getInterfaceName(entityDto.getName());
        MotechDataService service = (MotechDataService) ServiceRetriever.getService(entitiesBundle.getBundleContext(), serviceName);

        Assert.assertEquals(service.retrieveAll().size(), INSTANCES);
    }

    private void verifyDDEInstancesAreCreated()
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        WebApplicationContext context = ServiceRetriever.getWebAppContext(bundleContext, MDS_ENTITIES_SYMBOLIC_NAME);
        SampleService sampleService = context.getBean(SampleService.class);

        EntityDto entityDto = entityService.getEntityByClassName(Sample.class.getName());
        generator.generateDummyInstances(entityDto.getId(), INSTANCES);

        Assert.assertEquals(sampleService.retrieveAll().size(), INSTANCES);
    }
}
