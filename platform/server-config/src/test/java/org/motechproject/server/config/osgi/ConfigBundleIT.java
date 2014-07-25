package org.motechproject.server.config.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ConfigBundleIT extends BasePaxIT {

    @Inject
    private ConfigurationService configurationService;

    @Test
    public void testConfigBundle() throws Exception {
        assertNotNull(configurationService.loadBootstrapConfig().getSqlConfig().getUrl());
    }
}
