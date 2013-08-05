package org.motechproject.tasks.annotations;

import org.motechproject.tasks.domain.ParameterType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks method parameter to be treated as action parameter.
 * <p/>
 * Each parameter in the given method has to have this annotation otherwise it will be
 * a problem with the proper execution of the channel action.
 *
 * @see TaskAction
 * @see TaskChannel
 * @see TaskAnnotationBeanPostProcessor
 * @since 0.19
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TaskActionParam {

    String displayName();

    String key();

    ParameterType type() default ParameterType.UNICODE;

    boolean required() default true;

}
