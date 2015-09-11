package org.motechproject.mds.util;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.state.StateManagerImpl;
import org.motechproject.mds.ex.MdsException;
import org.motechproject.mds.ex.object.PropertyCopyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * This is a helper class, used to invoke operations on instance state manager
 */
public final class StateManagerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateManagerUtil.class);

    /**
     * Sets the transaction version to the instance state manager. Version value will be retrieved from the instance.
     *
     * @param instance  the instance from which state manager will be retrieved
     * @param versionFieldName the name of the version field
     */
    public static void setTransactionVersion(Object instance, String versionFieldName) {
        try {
            setTransactionVersion(instance, PropertyUtil.getProperty(instance, versionFieldName), versionFieldName);
        } catch (IllegalAccessException | InvocationTargetException| NoSuchMethodException e) {
            throw new PropertyCopyException("Unable to copy properties for " + instance.getClass().getName(), e);
        }
    }

    /**
     * Sets the given transaction version to the instance state manager.
     *
     * @param instance the instance from which state manager will be retrieved
     * @param version the transaction version
     * @param versionFieldName the name of the version field
     */
    public static void setTransactionVersion(Object instance, Object version, String versionFieldName) {
        try {
            StateManagerImpl stateManager = getStateManager(instance);
            stateManager.setVersion(version);

            AbstractClassMetaData cmd = stateManager.getClassMetaData();
            int fieldPosition = cmd.getAbsolutePositionOfMember(versionFieldName);
            boolean[] dirtyFields = getDirtyFields(stateManager);
            //we must mark version field as non dirty
            dirtyFields[fieldPosition] = false;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new MdsException("Cannot set proper transaction version", e);
        }
    }

    private static StateManagerImpl getStateManager(Object instance) throws IllegalAccessException {
        java.lang.reflect.Field dnStateManagerField;
        Class clazz = instance.getClass();
        do {
            dnStateManagerField = getFieldSafe(clazz, "dnStateManager");
            clazz = clazz.getSuperclass();
        } while (clazz.getSuperclass() != null || dnStateManagerField == null);

        if (dnStateManagerField == null) {
            throw new MdsException("Cannot find state manager for " + instance.getClass().getName());
        }

        dnStateManagerField.setAccessible(true);
        StateManagerImpl stateManager = (StateManagerImpl) dnStateManagerField.get(instance);
        dnStateManagerField.setAccessible(false);
        return stateManager;
    }

    private static boolean[] getDirtyFields(StateManagerImpl stateManager) throws IllegalAccessException, NoSuchFieldException {
        java.lang.reflect.Field dirtyFieldsField = stateManager.getClass().getSuperclass().
                getSuperclass().getDeclaredField("dirtyFields");
        dirtyFieldsField.setAccessible(true);
        boolean[] dirtyFields = (boolean[]) dirtyFieldsField.get(stateManager);
        dirtyFieldsField.setAccessible(false);

        return dirtyFields;
    }

    private static java.lang.reflect.Field getFieldSafe(Class clazz, String name) {
        java.lang.reflect.Field field = null;
        try {
            field = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            LOGGER.debug("Cannot find {} field in {}", name, clazz.getName());
        }
        return field;
    }

    private StateManagerUtil() {
    }
}
