package org.motechproject.config.service.impl;


import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertThat;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ConfigurationServiceIT extends BasePaxIT {

    @Inject
    private ConfigurationService configurationService;

    @Test
    public void shouldSaveBootstrapConfigToDefaultLocationAndLoadFromTheSameLocation() {
        BootstrapConfig existingBootstrapConfig = configurationService.loadBootstrapConfig();
        BootstrapConfig bootstrapConfig = new BootstrapConfig(existingBootstrapConfig.getSqlConfig(), "tenant-abc", existingBootstrapConfig.getConfigSource());
        configurationService.save(bootstrapConfig);

        assertThat(configurationService.loadBootstrapConfig(), IsEqual.equalTo(bootstrapConfig));
    }

    @Test
    public void shouldPersistAndRetrieveBundlePropertiesFromDatabase() throws IOException, InterruptedException {
        Properties newProperties = new Properties();
        newProperties.put("test_prop3", "test3");
        newProperties.put("test_prop", "test_1");
        newProperties.put("test_prop2", "test_other");
        configurationService.addOrUpdateProperties("test_module", "1.0.0", "test_module", "file1", newProperties, getDefaultProperties());

        Thread.sleep(2000);

        Properties retrievedProperties = configurationService.getModuleProperties("test_module", "file1", getDefaultProperties());
        assertThat(retrievedProperties, IsEqual.equalTo(newProperties));
    }

    private Properties getDefaultProperties() {
        Properties p = new Properties();
        p.put("test_prop", "test_1");
        p.put("test_prop2", "test_2");
        return p;
    }
}
