package org.motechproject.server.event.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yyonkov
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MotechListener {
    String[] subjects();

    MotechListenerType type() default MotechListenerType.MOTECH_EVENT;
}
