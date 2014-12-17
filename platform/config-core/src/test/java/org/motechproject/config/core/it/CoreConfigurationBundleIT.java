package org.motechproject.config.core.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CoreConfigurationBundleIT extends BasePaxIT {

    @Inject
    private CoreConfigurationService coreConfigurationService;

    @Override
    protected boolean shouldFakeModuleStartupEvent() {
        return false;
    }

    @Override
    protected Collection<String> getAdditionalTestDependencies() {
        return Arrays.asList("org.codehaus.jackson:org.motechproject.org.codehaus.jackson");
    }

    @Test
    public void testBootstrapConfigBundleIT() {
        BootstrapConfig bootstrapConfig = coreConfigurationService.loadBootstrapConfig();
        assertNotNull(bootstrapConfig);
        assertNotNull(bootstrapConfig.getSqlConfig());
        assertNotNull(bootstrapConfig.getConfigSource());
    }
}
