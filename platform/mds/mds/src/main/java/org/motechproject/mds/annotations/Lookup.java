package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>Lookup</code> annotation is used by developers to point methods, in classes that
 * implements {@link org.motechproject.mds.service.MotechDataService}, that should be mapped as
 * Motech Dataservices Lookups. The discovery logic for this annotation is done in
 * {@link org.motechproject.mds.annotations.internal.LookupProcessor}
 *
 * @see org.motechproject.mds.annotations.internal.LookupProcessor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lookup {

    /**
     * Sets the name of the lookup. By default the lookup name is equal to the words used as the
     * method name that are joined together with a space character.
     *
     * @return the name of the lookup.
     */
    String name() default "";

}
