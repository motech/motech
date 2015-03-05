package org.motechproject.bundle.extender;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * Creates the extender configuration for MOTECH, currently blueprint dependency wait time is the only option supported.
 * In order to change the blueprint extender dependency wait time used during platform runtime,
 * {@code org.motechproject.blueprint.dependencies.waittime} should be set with the wait time in milliseconds.
 * The default blueprint timeout is 5 minutes.
 */
public class MotechExtenderConfigFactory implements FactoryBean<Properties> {

    public static final String DEP_WAIT_TIME_ENV = "org.motechproject.blueprint.dependencies.waittime";
    public static final String DEP_WAIT_TIME_KEY = "dependencies.wait.time";

    @Override
    public Properties getObject() {
        Properties extenderConfig = new Properties();

        String waitTime = System.getProperty(DEP_WAIT_TIME_ENV);

        if (StringUtils.hasText(waitTime)) {
            extenderConfig.put(DEP_WAIT_TIME_KEY, waitTime);
        }

        return extenderConfig;
    }

    @Override
    public Class<?> getObjectType() {
        return Properties.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
