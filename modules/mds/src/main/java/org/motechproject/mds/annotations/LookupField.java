package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>@LookupField</code> annotation allows developers to point fields in Lookup method, that
 * should be mapped as Lookup fields for Developer Defined Lookup.
 *
 * The discovery logic for this annotation is done in <code>LookupProcessor</code>
 *
 * @see org.motechproject.mds.annotations.internal.LookupProcessor
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LookupField {
    String name() default "";
}
