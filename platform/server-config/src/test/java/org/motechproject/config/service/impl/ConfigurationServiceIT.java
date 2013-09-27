package org.motechproject.config.service.impl;


import org.apache.commons.io.FileUtils;
import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Before;
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

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class ConfigurationServiceIT {

    private File configdir = new File("/tmp");
    private String url = "http://1.2.3.4:5984";
    private String username = "user";
    private String password = "pass";
    private String tenantId = "tama";
    private ConfigSource configSource = ConfigSource.FILE;

    @Autowired
    private ConfigurationService configurationService;

    @Before
    public void setUp() throws IOException {
        FileUtils.writeStringToFile(new File(configdir, "bootstrap.properties"), getProperties());
    }

    @Test
    public void shouldReadBootstrapConfigFromBootstarpProvpertiesSpecifiedInDefaultLocation() {
        BootstrapConfig expectedConfig = new BootstrapConfig(new DBConfig(url, username, password), tenantId, configSource);
        assertThat(configurationService.loadBootstrapConfig(), IsEqual.equalTo(expectedConfig));
    }

    @After
    public void tearDown(){
        new File(configdir, "bootstrap.properties").delete();
    }

    private String getProperties() {
        return new StringBuilder(100)
                .append("db.url=").append(url).append("\n")
                .append("db.username=").append(username).append("\n")
                .append("db.password=").append(password).append("\n")
                .append("tenant.id=").append(tenantId).append("\n")
                .append("config.source=").append(configSource.getName()).toString();
    }
}
