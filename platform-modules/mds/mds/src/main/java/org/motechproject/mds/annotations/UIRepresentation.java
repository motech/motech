package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *The <code>UIRepresentation</code> annotation is used to mark a method
 * to provide a UIRepresentation for an entity. Method marked with this annotation
 * should have return type <code>String</code> and should not take any argument.
 * If multiple methods are marked with this annotation no method would be invoked for
 * UIRepresentation.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UIRepresentation {
}
