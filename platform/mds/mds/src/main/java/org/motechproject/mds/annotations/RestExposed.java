package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>RestExposed</code> annotation is used by developers to mark lookups that should be exposed
 * by REST. It is being processed by {@link org.motechproject.mds.annotations.internal.LookupProcessor}.
 *
 * @see org.motechproject.mds.annotations.internal.LookupProcessor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestExposed {

}
