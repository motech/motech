package org.motechproject.config.service.impl;


import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.motechproject.config.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/configContext.xml"})
public class ConfigurationServiceIT {

    private String url = "http://www.testurl.com";
    private String username = "test_usr";
    private String password = "test_pwd";
    private String tenantId = "test_tenentid";
    private ConfigSource configSource = ConfigSource.FILE;

    @Autowired
    private ConfigurationService configurationService;


    @Test
    public void shouldReadBootstrapConfigFromBootstarpProvpertiesSpecifiedInDefaultLocation() {
        BootstrapConfig expectedConfig = new BootstrapConfig(new DBConfig(url, username, password), tenantId, configSource);
        assertThat(configurationService.loadBootstrapConfig(), IsEqual.equalTo(expectedConfig));
    }


}
