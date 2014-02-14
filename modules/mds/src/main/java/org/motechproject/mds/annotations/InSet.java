package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must have value that will not be in defined set.
 * <p/>
 * Supported types are:
 * <ul>
 * <li>Integer</li>
 * <li>Double</li>
 * <li><code>int</code>, <code>double</code></li>
 * </ul>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InSet {

    /**
     * The <code>String</code> array representation of the values in which the value of the given
     * field should be.
     *
     * @return array of elements.
     */
    String[] value() default {};

}
