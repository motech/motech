package org.motechproject.server.config.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ConfigBundleIT extends BasePaxIT {

    @Inject
    private ConfigurationService configurationService;

    @Test
    public void testConfigBundle() throws Exception {
        assertNotNull(configurationService.loadBootstrapConfig().getCouchDbConfig().getUrl());
    }
}
