package org.motechproject.mds.listener;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.util.EntitiesClassListLoader;

import javax.jdo.Constants;
import java.util.Properties;

/**
 * This class adds listener entries to the properties passed to DataNucleus.
 * The classes for which listeners will be called are read from the entities file.
 * This class executes within the entities bundle, constructed through xml.
 */
public class JdoListenerRegister {

    private static final String LISTENER_KEY_PREFIX = Constants.PROPERTY_INSTANCE_LIFECYCLE_LISTENER + '.';

    public Properties addTrashHistoryListeners(Properties properties) {
        Properties resultProps = new Properties();

        resultProps.putAll(properties);

        resultProps.setProperty(LISTENER_KEY_PREFIX + TrashListener.class.getName(), EntitiesClassListLoader.entitiesStr());

        final String historyClassesStr = EntitiesClassListLoader.entitiesWithHistoryStr();
        if (StringUtils.isNotBlank(historyClassesStr)) {
            resultProps.setProperty(LISTENER_KEY_PREFIX + HistoryListener.class.getName(), historyClassesStr);
        }

        return resultProps;
    }
}
