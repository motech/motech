package org.motechproject.mds.listener.register;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.listener.proxy.ProxyJdoListener;
import org.motechproject.mds.util.EntitiesClassListLoader;

import javax.jdo.Constants;
import java.util.Properties;

/**
 * This class adds listener entries to the properties passed to DataNucleus.
 * The classes for which listeners will be called are read from the entities file.
 */
public class JdoListenerRegister {

    private static final String LISTENER_KEY_PREFIX = Constants.PROPERTY_INSTANCE_LIFECYCLE_LISTENER + '.';

    public Properties addJdoListener(Properties properties) {
        Properties resultProps = new Properties();

        resultProps.putAll(properties);

        final String entityWithListenerStr = EntitiesClassListLoader.entitiesWithListenerStr();
        if (StringUtils.isNotBlank(entityWithListenerStr)) {
           resultProps.setProperty(LISTENER_KEY_PREFIX + ProxyJdoListener.class.getName(), entityWithListenerStr);
        }

        return resultProps;
    }
}
