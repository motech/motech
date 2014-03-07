package org.motechproject.mds.service.impl.internal;

import org.apache.commons.beanutils.PropertyUtils;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.util.QueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.apache.commons.beanutils.PropertyUtils.isReadable;
import static org.apache.commons.beanutils.PropertyUtils.isWriteable;
import static org.apache.commons.beanutils.PropertyUtils.setProperty;
import static org.apache.commons.lang.StringUtils.uncapitalize;
import static org.motechproject.mds.util.QueryUtil.createDeclareParameters;
import static org.motechproject.mds.util.QueryUtil.createFilter;

/**
 * Default implementation of {@link org.motechproject.mds.service.HistoryService} interface.
 */
@Service
public class HistoryServiceImpl extends BaseMdsService implements HistoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryServiceImpl.class);

    @Override
    @Transactional
    public void record(Class<?> history, Object instance) {
        if (null != history) {
            LOGGER.debug("Recording history for: {}", instance.getClass().getName());

            PersistenceManager manager = getPersistenceManagerFactory().getPersistenceManager();
            Long objId = getInstanceId(instance);

            Query query = manager.newQuery(history);
            query.setFilter(previousFilter(history));
            query.declareParameters(createDeclareParameters(objId));
            query.setUnique(true);

            Object previous = query.execute(objId);
            Object current = createCurrentHistory(history, instance);

            if (null == previous) {
                LOGGER.debug("Not found previous entry. Create a new history entry.");
                manager.makePersistent(current);
            } else {
                LOGGER.debug("Found previous entry.");
                safeSetProperty(previous, next(history), current);
                safeSetProperty(current, previous(history), previous);

                LOGGER.debug("Create a new history entry.");
                manager.makePersistent(current);

                LOGGER.debug("Update the previous history entry.");
                manager.makePersistent(previous);
            }

            LOGGER.debug("Recorded history for: {}", instance.getClass().getName());
        }
    }

    private Object createCurrentHistory(Class<?> historyClass, Object instance) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(instance);
        Object current = safeNewInstance(historyClass);

        for (PropertyDescriptor descriptor : descriptors) {
            String propertyName = descriptor.getName();

            if (!"class".equalsIgnoreCase(propertyName)) {
                Object value = safeGetProperty(instance, propertyName);
                safeSetProperty(current, propertyName, value);
            }
        }

        // the id will be set by database
        safeSetProperty(current, "id", null);

        // creates connection between instance object and history object
        Long id = getInstanceId(instance);
        safeSetProperty(current, currentVersion(historyClass), id);

        return current;
    }

    private Long getInstanceId(Object instance) {
        Number id = null;

        try {
            Object value = getProperty(instance, "id");

            if (value instanceof Number) {
                id = (Number) value;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error("There was a problem with getting instance id value.", e);
        }

        return null == id ? null : id.longValue();
    }

    private Object safeNewInstance(Class<?> historyClass) {
        Object record = null;

        try {
            record = historyClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("There was a problem with creating new instance of {}", historyClass);
            LOGGER.error("Because of: ", e);
        }

        return record;
    }

    private void safeSetProperty(Object bean, String name, Object value) {
        try {
            if (isWriteable(bean, name)) {
                setProperty(bean, name, value);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error(
                    "There was a problem with set value {} for property {} in bean: {}",
                    new Object[]{value, name, bean}
            );
            LOGGER.error("Because of: ", e);
        }
    }

    private Object safeGetProperty(Object bean, String name) {
        Object value = null;

        try {
            if (isReadable(bean, name)) {
                value = getProperty(bean, name);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error(
                    "There was a problem with get value of property {} in bean: {}", name, bean
            );
            LOGGER.error("Because of: ", e);
        }

        return value;
    }

    private String currentVersion(Class<?> historyClass) {
        String name = historyClass.getSimpleName() + "CurrentVersion";
        return uncapitalize(name);
    }

    private String previous(Class<?> historyClass) {
        return uncapitalize(historyClass.getSimpleName() + "Previous");
    }

    private String next(Class<?> historyClass) {
        return uncapitalize(historyClass.getSimpleName() + "Next");
    }

    private String previousFilter(Class<?> historyClass) {
        String filter = createFilter(currentVersion(historyClass));
        filter += QueryUtil.FILTER_AND + previous(historyClass) + " == null";

        return filter;
    }

}
