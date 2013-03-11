package org.motechproject.tasks.annotations;

import org.motechproject.tasks.domain.ParameterType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TaskActionParam {

    String displayName();

    String key();

    ParameterType type() default ParameterType.UNICODE;

}
