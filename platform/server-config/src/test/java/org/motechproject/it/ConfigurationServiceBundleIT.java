package org.motechproject.it;


import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.domain.MotechSettings;
import org.motechproject.config.domain.SettingsRecord;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.mds.util.Constants;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ConfigurationServiceBundleIT extends BasePaxIT {

    private static final String SERVER_URL_TEST = "http://localhost:8080/motech-platform-server";

    private String motechDir = new File(System.getProperty("user.home"), ".motech").getAbsolutePath();

    @Inject
    private ConfigurationService configurationService;

    @Test
    public void shouldSaveBootstrapConfigToDefaultLocationAndLoadFromTheSameLocation() {
        BootstrapConfig existingBootstrapConfig = configurationService.loadBootstrapConfig();
        BootstrapConfig bootstrapConfig = new BootstrapConfig(existingBootstrapConfig.getSqlConfig(), existingBootstrapConfig.getConfigSource(), "./felix", motechDir, existingBootstrapConfig.getQueueUrl(), existingBootstrapConfig.getActiveMqProperties());

        configurationService.save(bootstrapConfig);

        assertThat(configurationService.loadBootstrapConfig(), IsEqual.equalTo(bootstrapConfig));
    }

    @Test
    public void shouldPersistAndRetrieveBundlePropertiesFromDatabase() throws IOException, InterruptedException {
        Properties newProperties = new Properties();
        newProperties.put("test_prop3", "test3");
        newProperties.put("test_prop", "test_1");
        newProperties.put("test_prop2", "test_other");
        configurationService.addOrUpdateProperties("test_module", "1.0.0", "file1", newProperties, getDefaultProperties());

        Thread.sleep(2000);

        Properties retrievedProperties = configurationService.getBundleProperties("test_module", "file1", getDefaultProperties());
        assertThat(retrievedProperties, IsEqual.equalTo(newProperties));
    }

    @Test
    public void shouldGetProperlyPlatformSettings() {
        prepareDbSettingsRecord();

        MotechSettings settingsRecord = configurationService.getPlatformSettings();

        assertEquals(SERVER_URL_TEST, settingsRecord.getServerUrl());
        assertEquals(Integer.valueOf(80), settingsRecord.getSessionTimeout());
        assertEquals(true, settingsRecord.getEmailRequired());
    }

    private Properties getDefaultProperties() {
        Properties p = new Properties();
        p.put("test_prop", "test_1");
        p.put("test_prop2", "test_2");
        return p;
    }

    private void prepareDbSettingsRecord() {
        SettingsRecord settingsRecord = configurationService.loadDefaultConfig();
        Map<String, String> platformSettings = new HashMap<>();

        platformSettings.put(ConfigurationConstants.SERVER_URL, SERVER_URL_TEST);
        platformSettings.put(ConfigurationConstants.SESSION_TIMEOUT, "80");
        platformSettings.put(ConfigurationConstants.EMAIL_REQUIRED, "true");

        settingsRecord.setPlatformInitialized(true);
        settingsRecord.setPlatformSettings(platformSettings);

        configurationService.addOrUpdateSettings(settingsRecord);
    }
}
