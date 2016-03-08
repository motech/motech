package org.motechproject.event.listener.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>MotechParam</code> annotation is used by developers to specify parameters
 * in a method which handles event. The parameters are used only in the
 * {@link org.motechproject.event.listener.annotations.MotechListenerNamedParametersProxy}
 * type of listener.
 * <p/>
 * This annotation is processed by
 * {@link org.motechproject.event.listener.annotations.MotechListenerNamedParametersProxy#callHandler(org.motechproject.event.MotechEvent)}
 *
 * @see org.motechproject.event.listener.annotations.MotechListenerNamedParametersProxy
 */
@Target({ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MotechParam {
    /**
     * Sets the name of the parameter.
     *
     * @return the name of the parameter.
     */
    String value();
}
