package org.motechproject.mds.display;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.annotations.UIRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 *The <code>UiRepresentationUtil</code> class provides a mechanism for finding methods with
 * the {@link org.motechproject.mds.annotations.UIRepresentation} and invoke the method with the annotation
 * to provide a UIRepresentation string for an entity instance.
 */
public final class UIRepresentationUtil {

    private UIRepresentationUtil() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UIRepresentationUtil.class);

    public static String uiRepresentationString (Object instance) {
        if(instance == null) {
            return "";
        }
        Class clazz = instance.getClass();
        List<Method> methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(UIRepresentation.class)) {
                if (ArrayUtils.isEmpty(method.getParameterTypes()) && String.class.equals(method.getReturnType())){
                    methods.add(method);
                } else {
                    LOGGER.error("Method {} is not a valid method for @UIRepresentation annotation." +
                            " It should have String as return type and should not have any parameters.", method.getName());
                }
            }
        }
        String uiRepresentation = null;
        if (methods.size() == 0) {
            LOGGER.debug("No Method with @UIRepresentation annotation in class {}", clazz.getName());
        } else if (methods.size() > 1 ) {
            LOGGER.error("Multiple Methods with @UIRepresentation annotation in class {}. " +
                    "@UIRepresentation annotation won't be respected for this class." +
                    "Only one method should be annotated with @UIRepresentation", clazz.getName());
        } else {
            try {
                uiRepresentation = (String) methods.get(0).invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("Error while retrieving @UIRepresentation value for {}", clazz.getName(), e);
            }
        }
        return uiRepresentation;
    }
}
