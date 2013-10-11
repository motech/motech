package org.motechproject.config.service.impl;


import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.motechproject.config.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:testApplicationPlatformConfig.xml"})
public class ConfigurationServiceIT {

    private String url = "http://www.testurl.com";
    private String username = "test_usr";
    private String password = "test_pwd";
    private String tenantId = "test_tenentid";
    private ConfigSource configSourceFile = ConfigSource.FILE;
    private ConfigSource configSourceUI = ConfigSource.UI;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private PropertiesConfiguration propertiesConfiguration;

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(new File(propertiesConfiguration.getString("config.location")));
    }

    @Test
    public void shouldSaveBootstrapConfigToDefaultLocationAndLoadFromTheSameLocation() {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig(url, username, password), tenantId, configSourceFile);
        configurationService.save(bootstrapConfig);

        assertThat(configurationService.loadBootstrapConfig(), IsEqual.equalTo(bootstrapConfig));
    }

    @Test
    public void shouldPersistAndRetrieveBundlePropertiesFromDatabase() throws IOException, InterruptedException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig(url, username, password), tenantId, configSourceUI);
        configurationService.save(bootstrapConfig);

        Properties newProperties = new Properties();
        newProperties.put("test_prop3", "test3");
        newProperties.put("test_prop", "test_1");
        newProperties.put("test_prop2", "test_other");
        configurationService.updateProperties("test_module", "file1", getDefaultProperties(), newProperties);

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
