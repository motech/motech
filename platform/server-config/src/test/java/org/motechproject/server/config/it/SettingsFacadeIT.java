package org.motechproject.server.config.it;


import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileNotFoundException;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class SettingsFacadeIT {

    private SettingsFacade settingsFacade;

    private CouchDbInstance instance;

    @Autowired
    private Properties couchdbProperties;

    @Before
    public void before() throws Exception {
        settingsFacade = new SettingsFacade();
        HttpClientFactoryBean httpClientFactoryBean = new HttpClientFactoryBean();
        httpClientFactoryBean.setProperties(couchdbProperties);
        httpClientFactoryBean.setTestConnectionAtStartup(true);
        httpClientFactoryBean.setCaching(false);
        httpClientFactoryBean.afterPropertiesSet();
        instance = new StdCouchDbInstance(httpClientFactoryBean.getObject());
    }

    @Test
    public void shouldPrefixMotechAppName_WhenPlatformSettingsServiceNotAvailable() throws FileNotFoundException {
        CouchDbConnector connector = settingsFacade.getConnector("dbname", "couchdb.properties");
        String databaseName = connector.getDatabaseName();
        assertEquals("testprefix_dbname", databaseName);
        instance.deleteDatabase("testprefix_dbname");
    }
}
