package org.motechproject.mds.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * The <code>Loader</code> is an abstract class that checks if all class dependencies to the given
 * class definition are resolved. If not then the missing class name is taken from exception and
 * the {@link #doWhenClassNotFound(String)} method is executed.
 *
 * @param <T> the type of argument data
 */
public abstract class Loader<T> {
    public abstract Class<?> getClassDefinition(T arg);

    public abstract void doWhenClassNotFound(String name);

    public Class<?> loadClass(T arg) {
        Class<?> definition = getClassDefinition(arg);

        while (true) {
            try {
                for (Field field : definition.getDeclaredFields()) {
                    field.getGenericType();
                    field.getDeclaredAnnotations();
                }

                for (Method method : definition.getDeclaredMethods()) {
                    method.getGenericExceptionTypes();
                    method.getGenericParameterTypes();
                    method.getGenericReturnType();
                }
                break;
            } catch (NoClassDefFoundError e) {
                Throwable cause = e.getCause();
                String name;

                if (cause instanceof ClassNotFoundException) {
                    name = cause.getMessage();
                } else {
                    String message = e.getMessage();
                    name = message.substring(1, message.length() - 1);
                }

                doWhenClassNotFound(name);
            } catch (TypeNotPresentException e) {
                // generic type not available, we must load
                doWhenClassNotFound(e.typeName());
            }
        }

        return definition;
    }

}
