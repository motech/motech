package org.motechproject.mds.listener.register;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.listener.proxy.ProxyJdoListener;
import org.motechproject.mds.listener.records.HistoryListener;
import org.motechproject.mds.listener.records.TrashListener;

import javax.jdo.Constants;
import java.util.Properties;

/**
 * This class adds listener entries to the properties passed to DataNucleus.
 * The classes for which listeners will be called are read from the entities file.
 * This class executes within the entities bundle, constructed through xml.
 */
public class JdoListenerRegister {

    private static final String LISTENER_KEY_PREFIX = Constants.PROPERTY_INSTANCE_LIFECYCLE_LISTENER + '.';

    public Properties addJdoListeners(Properties properties) {
        Properties resultProps = new Properties();
        resultProps.putAll(properties);

        addEntityListener(resultProps);
        addTrashHistoryListeners(resultProps);

        return resultProps;
    }

    private void addEntityListener(Properties properties) {
        final String entityWithListenerStr = EntitiesClassListLoader.entitiesWithListenerStr();
        if (StringUtils.isNotBlank(entityWithListenerStr)) {
            properties.setProperty(LISTENER_KEY_PREFIX + ProxyJdoListener.class.getName(), entityWithListenerStr);
        }
    }

    private void addTrashHistoryListeners(Properties properties) {
        properties.setProperty(LISTENER_KEY_PREFIX + TrashListener.class.getName(), EntitiesClassListLoader.entitiesStr());

        final String historyClassesStr = EntitiesClassListLoader.entitiesWithHistoryStr();
        if (StringUtils.isNotBlank(historyClassesStr)) {
            properties.setProperty(LISTENER_KEY_PREFIX + HistoryListener.class.getName(), historyClassesStr);
        }
    }
}
