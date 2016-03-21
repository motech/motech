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
 * The <code>UiRepresentationUtil</code> class provides a mechanism for finding methods with
 * the {@link org.motechproject.mds.annotations.UIRepresentation} and invoke the method with the annotation
 * to provide a UIRepresentation string for an entity instance.
 */
public final class UIRepresentationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(UIRepresentationUtil.class);

    private UIRepresentationUtil() {
    }

    /**
     * Looks for a method annotated {@link UIRepresentation} in the provided instance and invokes it
     * in order to fetch the String representation, provided by the annotated method. This method is
     * null-safe and will return empty string for null inputs. This method will return {@literal null}
     * if the contract for this method is not fulfilled. This method expects that:
     * <p><ul>
     *     <li>There's exactly one method annotated {@link UIRepresentation} in the provided instance object</li>
     *     <li>The annotated method returns {@link String}</li>
     *     <li>The annotated method does not take any parameters</li>
     * </ul></p>
     * If the contract is fulfilled, the annotated method of the instance is invoked and the result of the
     * annotated method is returned by this method.
     *
     * @param instance an instance for which we wish to get UI String representation
     * @return UI representation of the provided instance, as returned by the method annotated {@link UIRepresentation},
     *         {@literal null} if provided instance does not fulfill this method's contract or empty string if the instance
     *         passed to this method is {@literal null}
     */
    public static String uiRepresentationString(Object instance) {
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
        if (methods.isEmpty()) {
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
