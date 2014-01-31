package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.apache.commons.lang.StringUtils.EMPTY;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Field {

    String displayName() default EMPTY;

    String name() default EMPTY;

    boolean required() default false;

    String defaultValue() default EMPTY;

    String tooltip() default EMPTY;

}
