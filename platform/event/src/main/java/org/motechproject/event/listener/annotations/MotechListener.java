package org.motechproject.event.listener.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>MotechListener</code> annotation is used by developers to specify which
 * method should be invoked when an event with a particular subject will be fired.
 * <p/>
 * This annotation is processed by
 * {@link org.motechproject.event.listener.proxy.EventAnnotationBeanPostProcessor}
 *
 * @author yyonkov
 * @see org.motechproject.event.listener.proxy.EventAnnotationBeanPostProcessor
 */
@Target({ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MotechListener {
    /**
     * Sets the event subjects that a listener is interested in.
     *
     * @return the list of subjects.
     */
    String[] subjects();

    /**
     * Sets the type specified in {@link MotechListenerType}
     * enum, that is: {@link MotechListenerType#MOTECH_EVENT}
     * or {@link MotechListenerType#NAMED_PARAMETERS}.
     * By default the type is {@link MotechListenerType#MOTECH_EVENT}.
     *
     * @return the value of a {@link MotechListenerType}
     */
    MotechListenerType type() default MotechListenerType.MOTECH_EVENT;
}
