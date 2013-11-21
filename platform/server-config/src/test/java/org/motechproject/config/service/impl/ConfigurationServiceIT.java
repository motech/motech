package org.motechproject.config.service.impl;


import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class ConfigurationServiceIT {

    private String url = "http://www.testurl.com";
    private String username = "test_usr";
    private String password = "test_pwd";
    private String tenantId = "test_tenentid";
    private ConfigSource configSourceFile = ConfigSource.FILE;
    private ConfigSource configSourceUI = ConfigSource.UI;

    @Autowired
    private ConfigurationService configurationService;

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
        configurationService.loadBootstrapConfig();

        Properties newProperties = new Properties();
        newProperties.put("test_prop3", "test3");
        newProperties.put("test_prop", "test_1");
        newProperties.put("test_prop2", "test_other");
        configurationService.addOrUpdateProperties("test_module", "file1", newProperties, getDefaultProperties());

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
